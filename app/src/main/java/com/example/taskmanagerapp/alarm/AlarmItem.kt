package com.example.taskmanagerapp.alarm

import java.io.Serializable
import java.time.LocalDateTime

data class AlarmItem(
    val time: LocalDateTime,
    val message: String
): Serializable
