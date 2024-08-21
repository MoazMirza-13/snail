package com.gacha

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.telephony.TelephonyManager
import android.util.Log

class PhoneCallReceiver : BroadcastReceiver() {
    private var wasRinging = false
    private var wasOffhook = false

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        
        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                wasRinging = true
                wasOffhook = false
                Log.d("PhoneCallReceiver", "Incoming call detected")
            }
            
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                if (wasRinging) {
                    // Call was answered
                    playSound(context)
                    wasRinging = false
                }
                wasOffhook = true
                Log.d("PhoneCallReceiver", "Call connected")
            }
            
            TelephonyManager.EXTRA_STATE_IDLE -> {
                if (wasOffhook) {
                    // Call ended or disconnected
                    playSound(context)
                    wasOffhook = false
                } else if (wasRinging) {
                    // Call was rejected
                    playSound(context)
                    wasRinging = false
                }
                Log.d("PhoneCallReceiver", "Call ended or rejected")
            }
        }
    }

    private fun playSound(context: Context) {
    Log.d("PhoneCallReceiver", "Attempting to play sound")
    try {
        val mediaPlayer = MediaPlayer.create(context, R.raw.gacha)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            Log.d("PhoneCallReceiver", "Sound playback completed")
            it.release() // Release resources after playing the sound
        }
        Log.d("PhoneCallReceiver", "Sound playback started")
    } catch (e: Exception) {
        Log.e("PhoneCallReceiver", "Error playing sound", e)
    }
}

}
