package com.example.taskmanagerapp

import android.app.NotificationManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.taskmanagerapp.alarm.notification.InAppNotification
import com.example.taskmanagerapp.alarm.sound.MediaPlayerService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //clear notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(InAppNotification.NOTIFICATION_ID)
        //stop a sound
        val serviceIntent = Intent(this, MediaPlayerService::class.java)
        stopService(serviceIntent)
    }
}