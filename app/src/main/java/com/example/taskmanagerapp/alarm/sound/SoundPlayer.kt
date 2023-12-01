package com.example.taskmanagerapp.alarm.sound

import android.content.Context

interface SoundPlayer {
    fun startPlay(context: Context)
    fun stopPlay()
}