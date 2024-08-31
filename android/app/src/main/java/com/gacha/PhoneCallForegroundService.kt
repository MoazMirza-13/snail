package com.gacha

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class PhoneCallForegroundService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        Log.d("PhoneCallForegroundService", "Service created")

        // Start foreground service
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("PhoneCallForegroundService", "Service started and running")
        playSound()
        return START_STICKY
    }

    private fun startForegroundService() {
        val notificationId = 1
        val channelId = "PhoneCallServiceChannel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Phone Call Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Phone Call Service")
            .setContentText("The service is running...")
            .setSmallIcon(R.drawable.ic_launcher_round)
            .build()

        startForeground(notificationId, notification)
    }

private fun playSound() {
    try {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        
        // Attempt to route the audio to the loudspeaker
        try {
            audioManager.mode = AudioManager.MODE_IN_COMMUNICATION // Try to set the mode
            audioManager.isSpeakerphoneOn = true // Try to route to loudspeaker
            Log.d("PhoneCallForegroundService", "Speakerphone set to ON")
        } catch (e: Exception) {
            Log.e("PhoneCallForegroundService", "Error setting speakerphone on", e)
        }

        mediaPlayer = MediaPlayer()
        val afd = resources.openRawResourceFd(R.raw.gacha)
        mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        afd.close()

        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )

        mediaPlayer.prepare()
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            Log.d("PhoneCallForegroundService", "Sound playback completed")
            it.release()

      /*      // Optionally reset the speakerphone setting
            try {
                audioManager.isSpeakerphoneOn = false
                audioManager.mode = AudioManager.MODE_NORMAL
                Log.d("PhoneCallForegroundService", "Speakerphone set to OFF, mode set to NORMAL")
            } catch (e: Exception) {
                Log.e("PhoneCallForegroundService", "Error resetting speakerphone or mode", e)
            } */
        } 

        Log.d("PhoneCallForegroundService", "Sound playback started")
    } catch (e: Exception) {
        Log.e("PhoneCallForegroundService", "Error during sound playback", e)
    }
}




    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
