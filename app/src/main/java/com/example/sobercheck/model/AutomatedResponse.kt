package com.example.sobercheck.model

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.preference.PreferenceManager
import com.example.sobercheck.R

class AutomatedResponse {

    fun respond(activity: Activity, context: Context, mediaPlayer: MediaPlayer): BooleanArray {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val responded = booleanArrayOf(false, false, false, false)

        val countDownTimer = object : CountDownTimer(CountDown.TIME.time, CountDown.STEP.time) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                val alertOn = sharedPreferences.getBoolean(
                    context.resources.getString(R.string.pref_alert),
                    false
                )
                val callOn = sharedPreferences.getBoolean(
                    context.resources.getString(R.string.pref_call),
                    false
                )
                val smsOn = sharedPreferences.getBoolean(
                    context.resources.getString(R.string.pref_sms),
                    false
                )
                val orderAnUberOn =
                    sharedPreferences.getBoolean(
                        context.resources.getString(R.string.pref_uber),
                        false
                    )

                if (alertOn) {
                    mediaPlayer.isLooping = true
                    mediaPlayer.start()
                    responded[0] = true
                }
                val telephony = Telephony()
                if (smsOn) {
                    telephony.sendSMS(context)
                    responded[1] = true
                }
                if (callOn) {
                    telephony.makeCall(activity)
                    responded[2] = true
                }
                if (orderAnUberOn) {
                    UberService().getUberDeepLink(context).execute()
                    responded[3] = true
                }
            }
        }
        countDownTimer.start()
        return responded
    }
}