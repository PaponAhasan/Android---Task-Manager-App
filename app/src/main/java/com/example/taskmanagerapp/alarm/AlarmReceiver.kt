package com.example.taskmanagerapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.taskmanagerapp.alarm.notification.InAppNotification
import com.example.taskmanagerapp.alarm.sound.MediaPlayerService
import com.example.taskmanagerapp.model.TaskList

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {
        /*Handle the alarm action here*/

        //get alarm item message
        val alarmItem = intent?.getSerializableExtra("alarmItem") as TaskList
        val message = alarmItem.titleText
        Log.d("TAG", message)

        //show a notification
        val service = InAppNotification(context!!)
        service.showNotification(alarmItem)

        //play a media
        val serviceIntent = Intent(context, MediaPlayerService::class.java)
        context.startService(serviceIntent)

    }
}