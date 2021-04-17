package com.example.sobercheck.model

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.sobercheck.R

class AutomatedResponse {

    internal var responded = booleanArrayOf(false, false, false, false)
    fun respond(activity: Activity, context: Context, mediaPlayer: MediaPlayer) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val countDownTimer =
            object : CountDownTimer(CountDown.TIME_5_SECONDS.time, CountDown.STEP.time) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    val alertOn = sharedPreferences.getBoolean(
                        context.resources.getString(R.string.pref_alert),
                        false
                    )
                    Log.d(TAG, "Alert ON : $alertOn")
                    val smsOn = sharedPreferences.getBoolean(
                        context.resources.getString(R.string.pref_sms),
                    false
                )
                Log.d(TAG, "SMS ON : $alertOn")
                val callOn = sharedPreferences.getBoolean(
                    context.resources.getString(R.string.pref_call),
                    false
                )
                Log.d(TAG, "Call ON : $alertOn")
                val orderAnUberOn =
                    sharedPreferences.getBoolean(
                        context.resources.getString(R.string.pref_uber),
                        false
                    )
                Log.d(TAG, "Uber Request ON : $alertOn")

                if (alertOn) {
                    mediaPlayer.isLooping = true
                    mediaPlayer.start()
                    responded[0] = true
                    Log.d(TAG, "Alert response : ${responded[2]}")
                }
                val telephony = Telephony()
                if (smsOn) {
                    telephony.sendSMS(context)
                    responded[1] = true
                    Log.d(TAG, "SMS response : ${responded[1]}")
                }
                if (callOn) {
                    telephony.makeCall(activity)
                    responded[2] = true
                    Log.d(TAG, "Call response : ${responded[2]}")
                }
                if (orderAnUberOn) {
                    UberService().getUberDeepLink(context).execute()
                    responded[3] = true
                    Log.d(TAG, "Uber response : ${responded[3]}")
                }
            }
        }
        countDownTimer.start()
    }

    companion object {
        const val TAG: String = "AutomatedResponse"
    }
}