package com.example.taskmanagerapp.alarm.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color

class ApplicationChannel: Application() {
    companion object{
        private const val ALARM_CHANNEL_NAME = "Upcoming Task"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Simple notification channel
        val channel = NotificationChannel(
            InAppNotification.CHANNEL_ID,
            ALARM_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setSound(null, null)
            lightColor = Color.GREEN
            enableLights(true)
            description = "Used For The Upcoming Task Notification"
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}