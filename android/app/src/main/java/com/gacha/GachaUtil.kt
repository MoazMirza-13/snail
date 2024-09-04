package com.gacha

import android.content.Context
import android.media.MediaPlayer
import android.media.AudioAttributes
import android.util.Log
import android.os.Handler

object GachaUtil {

    fun playOffhookSound(context: Context) {
        try {
            val mediaPlayer = MediaPlayer()
            val afd = context.resources.openRawResourceFd(R.raw.gacha)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()

            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )

            mediaPlayer.prepare()

            Handler().postDelayed({
             mediaPlayer.start()
             mediaPlayer.setOnCompletionListener {
                 Log.d("PhoneCallReceiver", "Sound playback completed")
                 it.release()
             }
             Log.d("PhoneCallReceiver", "Sound playback started")
         }, 200) 

        } catch (e: Exception) {
            Log.e("SoundUtils", "Error during sound playback", e)
        }
    }
}
