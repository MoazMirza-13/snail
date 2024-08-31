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

    override fun onCreate() {
        super.onCreate()
        // Register a BroadcastReceiver to listen for custom intents
        val filter = IntentFilter().apply {
            addAction("com.gacha.ACTION_ENABLE_ACCESSIBILITY")
            addAction("com.gacha.ACTION_DISABLE_ACCESSIBILITY")
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
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d("PhoneCallAccessibilityService", "in service")
    
        // Only process events if the service is active
        if (!isServiceActive) return
    
        // Check if the event is from the dialer app and is a window state change
        if (event.packageName == "com.google.android.dialer" && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d("PhoneCallAccessibilityService", "Window state changed in dialer app")
    
            // Get the root node of the current window
            val rootNode = rootInActiveWindow
            if (rootNode == null) {
                Log.d("PhoneCallAccessibilityService", "Root node is null")
                return
            }
    
            // Attempt to toggle the speakerphone
            if (!toggleSpeakerphone(rootNode)) {
                Log.d("PhoneCallAccessibilityService", "Speakerphone button not found, returning")
                return
            }
        } else {
            Log.d("PhoneCallAccessibilityService", "Not in dialer app or not a window state change event")
        }
    }

    // Modified toggleSpeakerphone function to return true if the button is found and toggled
    private fun toggleSpeakerphone(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) return false
    
        // Search for the speakerphone button by content description "Speaker"
        val speakerButton = findNodeByContentDescription(rootNode, "Speaker")

        return if (speakerButton != null && speakerButton.isEnabled) {
            Log.d("PhoneCallAccessibilityService", "Speakerphone button found")
            
            // Delay the toggle action by 0.5 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                speakerButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d("PhoneCallAccessibilityService", "Speakerphone toggled")
            }, 500)
            true
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

    override fun onInterrupt() {
        // Handle interrupt
    }
}
