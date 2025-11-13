package vn.dainghia.callinspector.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import vn.dainghia.callinspector.R
import vn.dainghia.callinspector.data.model.TrueCallerResponse
import vn.dainghia.callinspector.data.repository.AppConfigRepository
import vn.dainghia.callinspector.data.repository.TrueCallerRepository
import vn.dainghia.callinspector.ui.composable.CallerInfoCard
import vn.dainghia.callinspector.ui.theme.CallInspectorTheme
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class CallInfoOverlayService : Service(), LifecycleOwner, SavedStateRegistryOwner {

    @Inject
    lateinit var appConfigRepository: AppConfigRepository

    @Inject
    lateinit var trueCallerRepository: TrueCallerRepository

    private val windowManager: WindowManager by lazy {
        applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
    }

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    private val savedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private var contentView: View? = null
    private var screenHeight: Int = 0
    private var viewHeight: Int = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            screenHeight = windowManager.currentWindowMetrics.bounds.height()
        } else @Suppress("DEPRECATION") {
            val display = windowManager.defaultDisplay
            val displayMetrics = DisplayMetrics()
            display.getMetrics(displayMetrics)
            screenHeight = displayMetrics.heightPixels
        }

        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager.registerTelephonyCallback(
                mainExecutor,
                object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                    override fun onCallStateChanged(state: Int) {
                        handleCallStateChanged(state)
                    }
                }
            )
        } else {
            @Suppress("DEPRECATION")
            telephonyManager.listen(
                object : PhoneStateListener() {
                    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                        handleCallStateChanged(state)
                    }
                },
                PhoneStateListener.LISTEN_CALL_STATE
            )
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        removeView()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            showNotification()
        }

        val phoneNumber = intent?.getStringExtra(PHONE_NUMBER_KEY) ?: ""
        lifecycle.coroutineScope.launch {
            val shouldShowOverlay = appConfigRepository.getShouldShowCallerInfoOverlay()
            if (!shouldShowOverlay && phoneNumber.isNotEmpty()) {
                return@launch
            }

            trueCallerRepository.searchCallerInfo(phoneNumber)
                .onSuccess(::showInfoCard)
                .onFailure { stopSelf() }
        }

        return START_REDELIVER_INTENT
    }

    private fun showInfoCard(trueCallerResponse: TrueCallerResponse) {
        val params = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START

        contentView = ComposeView(this).apply {
            setViewTreeSavedStateRegistryOwner(this@CallInfoOverlayService)
            setViewTreeLifecycleOwner(this@CallInfoOverlayService)

            setContent {
                CallInspectorTheme {
                    CallerInfoCard(
                        trueCallerResponse,
                        onCloseClick = { stopSelf() },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) { dragAmount ->
                        if (viewHeight == 0) return@CallerInfoCard

                        val minY = 0
                        val maxY = screenHeight - viewHeight
                        // Calculate the new proposed position
                        val newY = params.y + dragAmount.y.roundToInt()
                        // Clamp the new position within the bounds
                        params.y = newY.coerceIn(minY, maxY)

                        windowManager.updateViewLayout(contentView, params)
                    }
                }
            }
            viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        viewHeight = measuredHeight
                        viewTreeObserver.removeOnGlobalLayoutListener(this)

                        // Apply the centered position
                        val newY = (screenHeight - viewHeight) / 2
                        params.y = newY
                        windowManager.updateViewLayout(this@apply, params)
                    }
                }
            )
        }
        windowManager.addView(contentView, params)
    }

    private fun showNotification() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(ContextCompat.getString(this, R.string.notification_title))
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            ContextCompat.getString(this, R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(serviceChannel)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        removeView()
    }

    private fun removeView() {
        if (contentView == null) {
            return
        }
        windowManager.removeView(contentView)
        contentView = null
    }

    private fun handleCallStateChanged(state: Int) {
        if (state == TelephonyManager.CALL_STATE_IDLE) {
            stopSelf()
        }
    }

    companion object {
        private const val PHONE_NUMBER_KEY: String = "phone_number"
        private const val STATE_KEY: String = "state"
        private const val NOTIFICATION_CHANNEL_ID = "call_info_overlay_service_channel"
        private const val NOTIFICATION_ID = 1

        @RequiresApi(api = Build.VERSION_CODES.Q)
        fun createIntent(context: Context, phoneNumber: String): Intent =
            Intent(context, CallInfoOverlayService::class.java).apply {
                putExtra(PHONE_NUMBER_KEY, phoneNumber)
            }

        fun createIntent(context: Context, phoneNumber: String, state: String): Intent =
            Intent(context, CallInfoOverlayService::class.java).apply {
                putExtra(PHONE_NUMBER_KEY, phoneNumber)
                putExtra(STATE_KEY, state)
            }
    }
}