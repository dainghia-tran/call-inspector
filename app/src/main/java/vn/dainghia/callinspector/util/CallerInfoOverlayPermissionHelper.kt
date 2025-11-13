package vn.dainghia.callinspector.util

import android.Manifest.permission.READ_CALL_LOG
import android.Manifest.permission.READ_PHONE_STATE
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

/**
 * Utility object to check caller info overlay permissions and roles.
 * Does NOT handle requesting - use Compose permission APIs for that.
 */
object CallerInfoOverlayPermissionHelper {

    /**
     * Check if the app is eligible to show caller info overlay
     * - Android 10+: Requires READ_PHONE_STATE permission and CALL_SCREENING role
     * - Android 9: Requires READ_PHONE_STATE, READ_CALL_LOG, and SYSTEM_ALERT_WINDOW permissions
     */
    fun isEligible(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isEligibleAndroid10Plus(context)
        } else {
            isEligibleAndroid9(context)
        }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun isEligibleAndroid10Plus(context: Context): Boolean =
        hasPhoneStatePermission(context) && hasCallScreeningRole(context)

    private fun isEligibleAndroid9(context: Context): Boolean =
        hasPhoneStatePermission(context) &&
                hasCallLogPermission(context) &&
                hasSystemAlertWindowPermission(context)

    /**
     * Check if SYSTEM_ALERT_WINDOW permission is granted
     * This is required to display overlay windows on top of other apps
     */
    fun hasSystemAlertWindowPermission(context: Context): Boolean =
        Settings.canDrawOverlays(context)

    /**
     * Check if READ_PHONE_STATE permission is granted
     */
    fun hasPhoneStatePermission(context: Context): Boolean = ContextCompat.checkSelfPermission(
        context,
        READ_PHONE_STATE
    ) == PackageManager.PERMISSION_GRANTED

    /**
     * Check if READ_CALL_LOG permission is granted (Android 9 only)
     */
    fun hasCallLogPermission(context: Context): Boolean = ContextCompat.checkSelfPermission(
        context,
        READ_CALL_LOG
    ) == PackageManager.PERMISSION_GRANTED

    /**
     * Check if CALL_SCREENING role is held (Android 10+ only)
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun hasCallScreeningRole(context: Context): Boolean {
        val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
        return roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    }
}

