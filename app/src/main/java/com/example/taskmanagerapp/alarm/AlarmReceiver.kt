package com.example.taskmanagerapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.provider.Settings
import android.util.Log
import android.view.SurfaceHolder

class AlarmReceiver: BroadcastReceiver() {

    private lateinit var mp: MediaPlayer

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        println("Alarm triggered: $message")
        Log.d("TAG", message)
        mp = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI)
        // Check if MediaPlayer creation was successful
        mp.isLooping = true
        mp.start()
        // Release MediaPlayer when it's done playing
        mp.setOnCompletionListener {
            mp.release()
        }


//        val audioAttributes = AudioAttributes.Builder()
//            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//            .setUsage(AudioAttributes.USAGE_ALARM)
//            .build()
//
//        mp = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI, audioAttributes)
//
//        // Check if MediaPlayer creation was successful
//        mp.isLooping = true
//        mp.start()
//
//        // Release MediaPlayer when it's done playing
//        mp.setOnCompletionListener {
//            mp.release()
//        }

    }
}