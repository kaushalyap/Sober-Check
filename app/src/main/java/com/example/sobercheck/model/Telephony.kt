package com.example.sobercheck.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast
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
        Toast.makeText(
            context,
            context.resources.getString(R.string.message_sent),
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        const val MESSAGE_BODY: String =
            "Drunk detected!, Please look after the sender to avoid accidents. Sent by Sober Check app"
    }

    private fun getEmergencyContact(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(context.getString(R.string.pref_emergency_contact), "")
            .toString()
    }
}