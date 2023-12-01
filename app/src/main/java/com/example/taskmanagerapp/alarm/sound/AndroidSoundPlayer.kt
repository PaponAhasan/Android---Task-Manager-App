package com.example.taskmanagerapp.alarm.sound

import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings

class AndroidSoundPlayer: SoundPlayer {
    private lateinit var mp: MediaPlayer
    override fun startPlay(context: Context) {
        //play a sound
        mp = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI)
        // Check if MediaPlayer creation was successful
        mp.isLooping = true
        mp.start()
        // Release MediaPlayer when it's done playing
        mp.setOnCompletionListener {
            mp.release()
        }
    }

    override fun stopPlay() {
        if(mp.isPlaying){
            mp.stop()
        }
    }
}