
package com.gacha

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class WhatsAppListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn != null && sbn.packageName == "com.whatsapp") {
            val notification = sbn.notification
            val extras = notification.extras

            // Get the title and text from the notification
            val title = extras.getString("android.title")
            val text = extras.getCharSequence("android.text")?.toString()

            Log.d("WhatsAppNotification", "Notification Posted: Title: $title, Text: $text")

            // Check if this notification indicates an incoming call
            if (title != null && text.contains("Incoming")) {
                Log.d("WhatsAppNotification", "Incoming WhatsApp Call detected")
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Handle notification removal if needed
        Log.d("WhatsAppNotification", "Notification Removed")
    }
}
