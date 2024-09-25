package com.gacha

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import android.util.Log
import android.media.AudioManager
import android.os.Handler

class PhoneCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                Log.d("PhoneCallReceiver", "Ringer Mode: Normal")

                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                val sharedPreferences = context.getSharedPreferences("PhoneCallPrefs", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()

                Log.d("PhoneCallReceiver", "Phone State: $state")
                Log.d("PhoneCallReceiver", " Is Incoming: ${sharedPreferences.getBoolean("isIncoming", false)}")

                when (state) {
                    TelephonyManager.EXTRA_STATE_RINGING -> {
                        Log.d("PhoneCallReceiver", "in ringing block")
                        editor.putBoolean("isIncoming", true).apply()
                        Log.d("PhoneCallReceiver", "in ringing block. isIncoming set to true.")
                    }

                    TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                        Log.d("PhoneCallReceiver", "in offhook block")
                        if (sharedPreferences.getBoolean("isIncoming", false)) {
                            Log.d("PhoneCallReceiver", "in offhook block. this is an incoming call")
                        } else {
                            Log.d("PhoneCallReceiver", "in offhook block. this is an outgoing call in dialing phase")

                            // to detect answered call state
                            val enableOutgoingAccessibilityIntent = Intent("com.gacha.ACTION_ENABLE_OUTGOING_ACCESSIBILITY")
                            context.sendBroadcast(enableOutgoingAccessibilityIntent)
                        }

                        val enableAccessibilityIntent = Intent("com.gacha.ACTION_ENABLE_ACCESSIBILITY")
                        context.sendBroadcast(enableAccessibilityIntent)
                    }

                    TelephonyManager.EXTRA_STATE_IDLE -> {
                        // Reset the isIncoming flag when the call ends
                        editor.putBoolean("isIncoming", false).apply()

                        Handler().postDelayed({
                            playSound(context)
                        }, 1100)

                        val serviceIntent = Intent("com.gacha.ACTION_DISABLE_ACCESSIBILITY")
                        context.sendBroadcast(serviceIntent)
                    }
                }
            }
        }
    }

    private fun playSound(context: Context) {
        GachaUtil.playSound(context)
    }

}
