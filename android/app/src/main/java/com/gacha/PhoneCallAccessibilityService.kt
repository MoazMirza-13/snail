package com.gacha

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import android.os.Handler
import android.os.Looper

class PhoneCallAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d("PhoneCallAccessibilityService", "in service")

        if (event.packageName == "com.google.android.dialer" && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d("PhoneCallAccessibilityService", "Window state changed in dialer app")
            Log.d("PhoneCallAccessibilityService", "in app")

           
                val rootNode = rootInActiveWindow
                if (rootNode == null) {
                    Log.d("PhoneCallAccessibilityService", "Root node is null")
                } else {
             
                    toggleSpeakerphone(rootNode)
                }
       
        } else {
            Log.d("PhoneCallAccessibilityService", "not in dialer app")
        }
    }

    private fun toggleSpeakerphone(rootNode: AccessibilityNodeInfo?) {
        if (rootNode == null) return
    
        // Search for the speakerphone button by content description "Speaker"
        val speakerButton = findNodeByContentDescription(rootNode, "Speaker")
        
        if (speakerButton != null && speakerButton.isEnabled) {
            Log.d("PhoneCallAccessibilityService", "Speakerphone button found")
            // Toggle the speakerphone button
            speakerButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d("PhoneCallAccessibilityService", "Speakerphone toggled")
        } else {
            Log.d("PhoneCallAccessibilityService", "Speakerphone button not found or not enabled")
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
