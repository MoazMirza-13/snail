package com.gacha

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import android.os.Handler
import android.os.Looper

class PhoneCallAccessibilityService : AccessibilityService() {

    private var isServiceActive = false
    private var isOutgoingActive = false
    private var isSpeakerToggled = false 

    override fun onCreate() {
        super.onCreate()
        //  BroadcastReceiver Register
        val filter = IntentFilter().apply {
            addAction("com.gacha.ACTION_ENABLE_ACCESSIBILITY")
            addAction("com.gacha.ACTION_DISABLE_ACCESSIBILITY")
            addAction("com.gacha.ACTION_ENABLE_OUTGOING_ACCESSIBILITY")
        }
        registerReceiver(accessibilityReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(accessibilityReceiver)
    }

    private val accessibilityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.gacha.ACTION_ENABLE_ACCESSIBILITY" -> {
                    isServiceActive = true
                }
                "com.gacha.ACTION_DISABLE_ACCESSIBILITY" -> {
                    isServiceActive = false
                    isSpeakerToggled = false
                }
                "com.gacha.ACTION_ENABLE_OUTGOING_ACCESSIBILITY" -> {
                    isOutgoingActive = true
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d("PhoneCallAccessibilityService", "in service")

        if (!isServiceActive || isSpeakerToggled) return 

        if (event.packageName == "com.google.android.dialer" || event.packageName == "com.whatsapp") {
            Log.d("PhoneCallAccessibilityService", "Window state changed in dialer | whatsapp app")

            val rootNode = rootInActiveWindow
            if (rootNode == null) {
                Log.d("PhoneCallAccessibilityService", "Root node is null")
                return
            }

            if (isOutgoingActive) {
                Log.d("PhoneCallAccessibilityService", "in Outgoing block")

                val callDurationNode = findNodeByContentDescription(rootNode, "0 seconds")
                if (callDurationNode != null) {
                    Log.d("PhoneCallAccessibilityService", "Outgoing call has been answered")

                    isOutgoingActive = false
                }
            } else {
                // Attempt to toggle the speakerphone
                if (toggleSpeakerphone(rootNode)) {
                    isSpeakerToggled = true // Set flag after toggling
                }
            }
        } else {
            Log.d("PhoneCallAccessibilityService", "Not in dialer app or not a window state change event")
        }
    }

    private fun toggleSpeakerphone(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) return false
        
        val packageName = rootInActiveWindow?.packageName.toString()
        val speakerButton: AccessibilityNodeInfo?
    
        // Determine the correct content description based on the package name
        speakerButton = when (packageName) {
            "com.google.android.dialer" -> findNodeByContentDescription(rootNode, "Speaker")
            "com.whatsapp" -> findNodeByContentDescription(rootNode, "Turn speaker phone on")
            else -> null
        }
    
        return if (speakerButton != null && speakerButton.isEnabled) {
            // Check if the speakerphone is already on
            val isSpeakerOn = speakerButton.isSelected || (speakerButton.contentDescription?.toString() == "Speaker On")
    
            if (!isSpeakerOn) {
                Log.d("PhoneCallAccessibilityService", "Speakerphone button found and currently off")
                speakerButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d("PhoneCallAccessibilityService", "Speakerphone toggled")

                     // Play sound with delay for WhatsApp
                     if (packageName == "com.whatsapp") {
                        Handler(Looper.getMainLooper()).postDelayed({
                            playOffhookSound(this)
                        }, 700) 
                    } else {
                        playOffhookSound(this)
                    }
        
                    // Toggle off the speakerphone after delay
 if (packageName == "com.whatsapp") {
  Handler(Looper.getMainLooper()).postDelayed({
    if (speakerButton.isEnabled) {
        speakerButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        Log.d("PhoneCallAccessibilityService", "Speakerphone toggled off")
    }
}, 1400)
} else {
      Handler(Looper.getMainLooper()).postDelayed({
        if (speakerButton.isEnabled) {
            speakerButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d("PhoneCallAccessibilityService", "Speakerphone toggled off")
        }
    }, 900)
}

                true
            } else {
                Log.d("PhoneCallAccessibilityService", "Speakerphone is already on")
                playOffhookSound(this)

            true
            }
        } else {
            Log.d("PhoneCallAccessibilityService", "Speakerphone button not found or not enabled")
            false
        }
    }

    private fun findNodeByContentDescription(node: AccessibilityNodeInfo, contentDescription: String): AccessibilityNodeInfo? {
        // Check if node's content description matches the specified description
        if (node.contentDescription != null && node.contentDescription.toString() == contentDescription) {
            return node
        }
        // Recursively search children
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val result = findNodeByContentDescription(child, contentDescription)
            if (result != null) return result
        }
        return null
    }

    private fun playOffhookSound(context: Context) {
        GachaUtil.playOffhookSound(context)
    }

    override fun onInterrupt() {
        // Handle interrupt
    }
}
