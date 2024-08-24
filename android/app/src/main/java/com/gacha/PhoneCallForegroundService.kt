package com.gacha

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class PhoneCallForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("PhoneCallForegroundService", "Service created")

        // Start foreground service
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("PhoneCallForegroundService", "Service started and running")
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
