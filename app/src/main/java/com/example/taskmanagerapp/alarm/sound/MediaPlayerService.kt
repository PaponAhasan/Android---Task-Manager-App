package com.example.taskmanagerapp.alarm.sound

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings

class MediaPlayerService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer.start()
        return START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}