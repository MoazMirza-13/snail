package com.gacha

import android.content.Context
import android.media.MediaPlayer
import android.media.AudioAttributes
import android.util.Log
import android.os.Handler
import android.media.AudioManager

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

            mediaPlayer.setVolume(0.9f, 0.9f)
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

    fun playSound(context: Context) {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setDataSource(context, android.net.Uri.parse("android.resource://" + context.packageName + "/" + R.raw.gacha))

        mediaPlayer.setOnPreparedListener {
            it.setVolume(0.9f, 0.9f)
            it.start() // Only start when media player is ready
        }

        mediaPlayer.setOnCompletionListener {
            it.release()
        }

        mediaPlayer.setOnErrorListener { mp, what, extra ->
            Log.e("PhoneCallReceiver", "Error occurred while playing sound: $what, $extra")
            mp.release()
            true
        }

        mediaPlayer.prepareAsync() 
    }

}
