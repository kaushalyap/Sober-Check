package com.example.sobercheck.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.sobercheck.R

class Telephony {

    fun makeCall(activity: Activity) {
        val intent = Intent(
            Intent.ACTION_CALL,
            Uri.parse("tel:${getEmergencyContact(activity.baseContext)}")
        )
        activity.startActivity(intent)
    }

    fun sendSMS(context: Context) {
        val emergencyContactNo = getEmergencyContact(context)
        val smsManager: SmsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(emergencyContactNo, null, MESSAGE_BODY, null, null)
        Log.d(TAG, "SMS sent!")
    }

    private fun getEmergencyContact(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(context.getString(R.string.pref_emergency_contact), "")
            .toString()
    }

    companion object {
        const val TAG: String = "Telephony"
        const val MESSAGE_BODY: String =
            "Drunk detected!, Please look after the sender to avoid accidents. Sent by Sober Check app"
    }
}