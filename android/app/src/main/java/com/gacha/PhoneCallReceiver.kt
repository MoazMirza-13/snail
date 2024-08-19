package com.gacha

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class PhoneCallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        if (state == TelephonyManager.EXTRA_STATE_RINGING) {
            Log.d("PhoneCallReceiver", "Incoming call detected")
        } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
            Log.d("PhoneCallReceiver", "Call ended")
        }
    }
}
