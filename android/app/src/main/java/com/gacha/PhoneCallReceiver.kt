package com.gacha

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.telephony.TelephonyManager
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PhoneCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        Log.d("PhoneCallReceiver", "Phone State: $state")
        
        when (state) {
            // ringing state
            TelephonyManager.EXTRA_STATE_RINGING -> {
                Log.d("PhoneCallReceiver", "in RINGING state")
              /*  try {
                Log.d("PhoneCallReceiver", "Trying to play sound")
                    playSound(context)
                } catch (e: Exception) {
                    Log.e("PhoneCallReceiver", "Error playing sound", e)
                } */
            }
            // idle state
             TelephonyManager.EXTRA_STATE_IDLE -> {
                Log.d("PhoneCallReceiver", "in IDLE state")
                try {
                Log.d("PhoneCallReceiver", "Trying to play sound")
                    playSound(context)
                } catch (e: Exception) {
                    Log.e("PhoneCallReceiver", "Error playing sound", e)
                }
            }
            // offhook state
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                Log.d("PhoneCallReceiver", "in offhook state")
                try {
                Log.d("PhoneCallReceiver", "Trying to play sound")
                    playSound(context)
                } catch (e: Exception) {
                    Log.e("PhoneCallReceiver", "Error playing sound", e)
                }
            }
        }
    }

    private fun playSound(context: Context) {
    Log.d("PhoneCallReceiver", "in playsound()")
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
