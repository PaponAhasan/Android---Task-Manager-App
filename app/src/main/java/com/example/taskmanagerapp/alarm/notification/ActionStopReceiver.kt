package com.example.taskmanagerapp.alarm.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.taskmanagerapp.alarm.AlarmItem
import com.example.taskmanagerapp.alarm.AndroidAlarmScheduler
import com.example.taskmanagerapp.alarm.sound.AndroidSoundPlayer

class ActionStopReceiver : BroadcastReceiver() {
    private lateinit var alarmScheduler: AndroidAlarmScheduler
    override fun onReceive(context: Context?, intent: Intent?) {
        // Schedule the alarm
        val alarmItem = intent?.getSerializableExtra("alarmItem") as AlarmItem
        alarmScheduler = AndroidAlarmScheduler(context!!)
        alarmScheduler.cancel(alarmItem)

        //play a sound
        val player = AndroidSoundPlayer()
        player.stopPlay()
    }
}

