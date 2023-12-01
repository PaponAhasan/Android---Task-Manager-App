package com.example.taskmanagerapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.taskmanagerapp.alarm.notification.InAppNotification
import com.example.taskmanagerapp.alarm.sound.AndroidSoundPlayer

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var mp: MediaPlayer

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {
        //Handle the alarm action here

        val alarmItem = intent?.getSerializableExtra("alarmItem") as AlarmItem
        val message = alarmItem.message
        Log.d("TAG", message)

        //show a notification
        val service = InAppNotification(context!!)
        service.showNotification(alarmItem)

        //play a sound
        val player = AndroidSoundPlayer()
        player.startPlay(context)
    }
}