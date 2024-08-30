package com.gacha

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class PhoneCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        Log.d("PhoneCallReceiver", "Phone State: $state")

        when (state) {
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                Log.d("PhoneCallReceiver", "Starting JobIntentService")
                val serviceIntent = Intent(context, PhoneCallForegroundServiceStarter::class.java)
                PhoneCallForegroundServiceStarter.enqueueWork(context, serviceIntent)
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                Log.d("PhoneCallReceiver", "in IDLE state")
            }
        }
    }
}
