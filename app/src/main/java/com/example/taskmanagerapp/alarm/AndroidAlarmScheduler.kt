package com.example.taskmanagerapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.taskmanagerapp.model.TaskList
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private lateinit var tasksTime: LocalDateTime
    private lateinit var calendar: Calendar
    override fun schedule(item: TaskList) {

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmItem", item)
        }

        alarmTime(item.dateText, item.timeText)

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

    private fun alarmTime(taskDate: String, taskTime: String) {
//        val today = Calendar.getInstance().time
//        val dateFormat = SimpleDateFormat("EEE-dd-MMM", Locale.getDefault())
//
//        val time = today.time
//        val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())
//
//        val todayDate = dateFormat.format(today)
//        val todayTime = timeFormat.format(time)
//
//        //current Time
//        val currentDateTimeString = "$todayDate $todayTime"
//        val currentDateFormatter =
//            SimpleDateFormat("EEE-dd-MMM HH:mm a", Locale.getDefault())
//        val currentDate = currentDateFormatter.parse(currentDateTimeString)
//        val currentSeconds = currentDate?.time?.div(1000)
//        //selected Time
//        val selectedDateTimeString = "$taskDate $taskTime"
//        val selectedDateFormatter =
//            SimpleDateFormat("EEE-dd-MMM HH:mm a", Locale.getDefault())
//        val selectedDate = selectedDateFormatter.parse(selectedDateTimeString)
//        var selectedSeconds = selectedDate?.time?.div(1000)
//
//        if (selectedSeconds != null && selectedSeconds < currentSeconds!!) {
//            // The selected time is in the past, add a day to it
//            val oneDayInSeconds = 24 * 60 * 60
//            selectedSeconds += oneDayInSeconds
//        }
//
//        val taskTime = selectedSeconds?.minus(currentSeconds!!)
//        tasksTime = LocalDateTime.now().plusSeconds(taskTime!!)
//        Log.d("MainActivity", "Seconds since epoch: $tasksTime")

        // Format task date and time strings into a Date object
        val dateFormat = SimpleDateFormat("E-dd-MMM", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())

        val date: Date = dateFormat.parse(taskDate) ?: return
        val time: Date = timeFormat.parse(taskTime) ?: return

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(
                Calendar.YEAR,
                Calendar.getInstance().get(Calendar.YEAR)
            ) // assuming you want to set the alarm for the current year
            set(Calendar.MONTH, date.month)
            set(Calendar.DAY_OF_MONTH, date.date)
            set(Calendar.HOUR_OF_DAY, time.hours)
            set(Calendar.MINUTE, time.minutes)
            set(Calendar.SECOND, 0)
        }

//        if (calendar.before(Calendar.getInstance())) {
//            calendar.add(Calendar.DATE, 1);
//        }

        this.calendar = calendar
    }
}