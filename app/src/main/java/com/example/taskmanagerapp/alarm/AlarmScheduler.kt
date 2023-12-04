package com.example.taskmanagerapp.alarm

import com.example.taskmanagerapp.model.TaskList

interface AlarmScheduler {
    fun schedule(item: TaskList)
    fun cancel(item: TaskList)
}