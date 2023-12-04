package com.example.taskmanagerapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.taskmanagerapp.model.TaskList
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private lateinit var calendar: Calendar
    override fun schedule(item: TaskList) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmItem", item)
        }

        createAnAlarm(item.dateText, item.timeText)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                PendingIntent.getBroadcast(
                    context,
                    item.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        } else {
            // For versions below Marshmallow, use setExact
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                PendingIntent.getBroadcast(
                    context,
                    item.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }

    override fun cancel(item: TaskList) {
        Log.d("TAG", "cancel")
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun createAnAlarm(taskDate: String, taskTime: String) {
        try {
            val items: List<String> = taskDate.split("-")
            val month = items[0]
            val dd = items[1]
            val year = items[2]

            val itemTime: List<String> = taskTime.split(":")
            val hour = itemTime[0]
            val min = itemTime[1]

            val curCal: Calendar = GregorianCalendar()
            curCal.timeInMillis = System.currentTimeMillis()
            val cal: Calendar = GregorianCalendar()
            cal[Calendar.HOUR_OF_DAY] = hour.toInt()
            cal[Calendar.MINUTE] = min.toInt()
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0
            cal[Calendar.DATE] = dd.toInt()

            this.calendar = cal
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}