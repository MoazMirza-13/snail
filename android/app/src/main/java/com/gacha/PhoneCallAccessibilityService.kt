package com.gacha

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class PhoneCallAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d("PhoneCallAccessibilityService", "Accessibility event received from package: ${event.packageName}, eventType: ${event.eventType}")

        if (event.packageName == "com.google.android.dialer" && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d("PhoneCallAccessibilityService", "Window state changed in dialer app")
            val rootNode = rootInActiveWindow
            toggleSpeakerphone(rootNode)
        }
    }

    private fun toggleSpeakerphone(rootNode: AccessibilityNodeInfo?) {
        if (rootNode == null) return

        // Recursively search for the speakerphone button by text or content description
        val speakerButton = findNodeByText(rootNode, "Speaker") ?: findNodeByDescription(rootNode, "Speakerphone")
        
        if (speakerButton != null && speakerButton.isEnabled) {
            Log.d("PhoneCallAccessibilityService", "Speakerphone button found")
            // Toggle the speakerphone button
            speakerButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d("PhoneCallAccessibilityService", "Speakerphone toggled")
        } else {
            Log.d("PhoneCallAccessibilityService", "Speakerphone button not found")
        }
    }

    private fun findNodeByText(node: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        if (node.text != null && node.text.toString().contains(text, ignoreCase = true)) {
            return node
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val result = findNodeByText(child, text)
            if (result != null) return result
        }
        return null
    }

    private fun findNodeByDescription(node: AccessibilityNodeInfo, description: String): AccessibilityNodeInfo? {
        if (node.contentDescription != null && node.contentDescription.toString().contains(description, ignoreCase = true)) {
            return node
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val result = findNodeByDescription(child, description)
            if (result != null) return result
        }
        return null
    }

    override fun onInterrupt() {
        // Handle interrupt
    }
}
