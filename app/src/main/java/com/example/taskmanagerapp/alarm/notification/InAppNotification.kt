package com.example.taskmanagerapp.alarm.notification

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.taskmanagerapp.MainActivity
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.alarm.AlarmItem

class InAppNotification(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    companion object {
        const val CHANNEL_ID = "upcoming_task"
        const val NOTIFICATION_ID = 0
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun showNotification(alarmItem: AlarmItem) {
        // Intent Activity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent =
            PendingIntent.getActivity(
                context, 1, intent,
                PendingIntent.FLAG_IMMUTABLE
            )

        // Intent Broadcast Receiver
        val actionIntent = Intent(context, ActionStopReceiver::class.java).apply {
            putExtra("alarmItem", alarmItem)
        }

        val actionPendingIntent =
            PendingIntent.getBroadcast(context, 2, actionIntent, PendingIntent.FLAG_IMMUTABLE)

        // Intent Service
        /*val intentService = Intent(context, NotificationIntentService::class.java).apply {
            putExtra("MESSAGE", "Toast Msg Service")
        }
        val serviceIntent =
            PendingIntent.getService(context, 3, intentService, PendingIntent.FLAG_IMMUTABLE)*/

        // create notification
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(alarmItem.message)
            .setContentText(alarmItem.time.toString())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            //automatically clear when the user clicks
            .setAutoCancel(true)
            //set visibility in lock screen
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            //small icon color
            .setColor(Color.GREEN)
            // set one time notification
            .setOnlyAlertOnce(false)
            //button action
            .addAction(R.drawable.ic_launcher_background, "STOP", actionPendingIntent)
            //.addAction(R.drawable.ic_launcher_foreground, "SERVICE", serviceIntent)
            .build()

        if (checkNotificationPermission()) {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                101
            )
        } else {
            return true
        }
        return false
    }
}