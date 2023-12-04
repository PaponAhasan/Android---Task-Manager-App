package com.example.taskmanagerapp.alarm.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmanagerapp.alarm.AndroidAlarmScheduler
import com.example.taskmanagerapp.alarm.sound.MediaPlayerService
import com.example.taskmanagerapp.model.TaskList

class ActionStopReceiver : BroadcastReceiver() {
    private lateinit var alarmScheduler: AndroidAlarmScheduler

    override fun onReceive(context: Context?, intent: Intent?) {

        //clear notification
        val notificationManager = context?.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(InAppNotification.NOTIFICATION_ID)

        // Schedule the alarm
        val alarmItem = intent?.getSerializableExtra("alarmItem") as TaskList
        alarmScheduler = AndroidAlarmScheduler(context)
        alarmScheduler.cancel(alarmItem)

        //stop a sound
        val serviceIntent = Intent(context, MediaPlayerService::class.java)
        context.stopService(serviceIntent)

        Toast.makeText(context, "STOP YOUR ALARM", Toast.LENGTH_SHORT).show()
    }
}

