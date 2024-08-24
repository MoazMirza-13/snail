package com.gacha

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import android.util.Log

class PhoneCallForegroundServiceStarter : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        Log.d("PhoneCallForegroundServiceStarter", "Handling work in JobIntentService")
        startForegroundService()
    }

    private fun startForegroundService() {
        Log.d("PhoneCallForegroundServiceStarter", "Starting Foreground Service")

        val serviceIntent = Intent(this, PhoneCallForegroundService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, PhoneCallForegroundServiceStarter::class.java, 1000, intent)
        }
    }
}
