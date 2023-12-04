package com.example.taskmanagerapp.alarm.notification

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.taskmanagerapp.MainActivity
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.model.TaskList

class InAppNotification(private val context: Context) {
    private val notificationManager = NotificationManagerCompat.from(context)
    companion object {
        const val CHANNEL_ID = "upcoming_task"
        const val NOTIFICATION_ID = 0
        const val ACTION_STOP = "STOP"
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun showNotification(alarmItem: TaskList) {
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
            action = ACTION_STOP
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

        // Fragment of NavDeepLink
        val bundle = Bundle()
        bundle.putSerializable("task", alarmItem)
        val deepLinkEditIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.addTaskFragment)
            .setArguments(bundle)
            .createPendingIntent()

        // create notification
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(alarmItem.titleText)
            .setContentText(alarmItem.timeText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            //set visibility in lock screen
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            //small icon color
            .setColor(Color.GREEN)
            // set one time notification
            .setOnlyAlertOnce(true)
            // Can't cancel your notification
            .setOngoing(true)
            //button action
            .addAction(
                R.drawable.ic_launcher_foreground,
                context.getString(R.string.edit),
                deepLinkEditIntent
            )
            .addAction(
                R.drawable.ic_launcher_background,
                context.getString(R.string.stop),
                actionPendingIntent
            )
            //automatically clear when the user clicks
            .setAutoCancel(false)

        if (checkNotificationPermission()) {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
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