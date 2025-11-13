package vn.dainghia.callinspector.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import vn.dainghia.callinspector.service.CallInfoOverlayService


class PhoneCallReceiver : BroadcastReceiver() {

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // On Android 10+, CallScreeningService handles incoming calls
            return
        }

        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE) ?: return
            val context = context ?: return
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val phoneNumber =
                    intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: return

                val intent = CallInfoOverlayService.createIntent(context, phoneNumber, state)
                context.startForegroundService(intent)
            }
        }
    }
}