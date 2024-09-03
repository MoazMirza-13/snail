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
                }
                "com.gacha.ACTION_ENABLE_OUTGOING_ACCESSIBILITY" -> {
                    isOutgoingActive = true
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d("PhoneCallAccessibilityService", "in service")

        if (!isServiceActive) return

        if (event.packageName == "com.google.android.dialer" && isServiceActive) {
            Log.d("PhoneCallAccessibilityService", "Window state changed in dialer app")

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
                    // Add logic for when the outgoing call is answered
                    isOutgoingActive = false // Reset flag to execute "else" block
                }
            } else {
                // Attempt to toggle the speakerphone
                if (!toggleSpeakerphone(rootNode)) {
                    Log.d("PhoneCallAccessibilityService", "Speakerphone button not found, returning")
                    return
                }
            }
        } else {
            Log.d("PhoneCallAccessibilityService", "Not in dialer app or not a window state change event")
        }
    }

    private fun toggleSpeakerphone(rootNode: AccessibilityNodeInfo?): Boolean {
        if (rootNode == null) return false
        
        val speakerButton = findNodeByContentDescription(rootNode, "Speaker")


        return if (speakerButton != null && speakerButton.isEnabled) {
            // Check if the speakerphone is already on
            val isSpeakerOn = speakerButton.isSelected || (speakerButton.contentDescription?.toString() == "Speaker On")

            if (!isSpeakerOn) {
                Log.d("PhoneCallAccessibilityService", "Speakerphone button found and currently off")
                speakerButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.d("PhoneCallAccessibilityService", "Speakerphone toggled")
                true
            } else {
                Log.d("PhoneCallAccessibilityService", "Speakerphone is already on")
                false
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

    override fun onInterrupt() {
        // Handle interrupt
    }
}
