package vn.dainghia.callinspector.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.Connection

class ScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val isIncoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING

        if (!isIncoming) {
            return
        }

        when (callDetails.callerNumberVerificationStatus) {
            Connection.VERIFICATION_STATUS_FAILED -> respondToCall(callDetails, isBlocked = true)
            else -> respondToCall(callDetails, isBlocked = false)
        }

        val phoneNumber = callDetails.handle.schemeSpecificPart
        val intent = CallInfoService.createIntent(application, phoneNumber)
        startService(intent)
    }

    private fun respondToCall(callDetails: Call.Details, isBlocked: Boolean) {
        val response = CallResponse.Builder()
            .setDisallowCall(isBlocked)
            .setRejectCall(isBlocked)
            .setSkipCallLog(isBlocked)
            .setSkipNotification(isBlocked)
            .build()

        respondToCall(callDetails, response)
    }
}