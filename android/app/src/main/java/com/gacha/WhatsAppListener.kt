package com.gacha

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.content.Intent
import android.os.Handler
import android.media.AudioManager

class WhatsAppListener : NotificationListenerService() {

    private var isIncomingCallDetected = false
    private var ongoingCallDetected = false


    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Check ringer mode inside the method
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            Log.d("WhatsAppNotification", "Ringer mode is not normal")
            return
        }

        if (sbn != null && sbn.packageName == "com.whatsapp") {
            val notification = sbn.notification
            val extras = notification.extras

            // Get the title and text from the notification
            val title = extras.getString("android.title")
            val text = extras.getCharSequence("android.text")?.toString()

            Log.d("WhatsAppNotification", "Notification Posted: Title: $title, Text: $text")

            if (title != null && text != null && text.contains("Incoming")) {
                Log.d("WhatsAppNotification", "Incoming true")
                isIncomingCallDetected = true
            }

            if (title != null && text != null && text.contains("Ongoing")) {
                Log.d("WhatsAppNotification", "in Ongoing block")
                ongoingCallDetected = true

                val enableAccessibilityIntent = Intent("com.gacha.ACTION_ENABLE_ACCESSIBILITY")
                sendBroadcast(enableAccessibilityIntent)
            }

        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (sbn != null && sbn.packageName == "com.whatsapp") {
            // Check ringer mode inside the method
            val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            if (audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
                Log.d("WhatsAppNotification", "Ringer mode is not normal")
                return
            }

            // for rejected calls
            if (isIncomingCallDetected && !ongoingCallDetected) {
                Log.d("WhatsAppNotification", "rejected if")

                Handler().postDelayed({
                    playSound()
                }, 300)
                isIncomingCallDetected = false 
                ongoingCallDetected = false

                val serviceIntent = Intent("com.gacha.ACTION_DISABLE_ACCESSIBILITY")
                sendBroadcast(serviceIntent)
            }
        }
    }

    private fun playSound() {
        GachaUtil.playSound(this)
    }
}
