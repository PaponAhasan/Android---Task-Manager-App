package com.example.taskmanagerapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tasks_table")
data class TaskList(
    @ColumnInfo(name = "titleText") val titleText: String,
    @ColumnInfo(name = "bodyText") val bodyText: String,
    @ColumnInfo(name = "eventText") val eventText: String,
    @ColumnInfo(name = "dateText") val dateText: String,
    @ColumnInfo(name = "timeText") val timeText: String,
    @ColumnInfo(name = "statusText") val statusText: Boolean
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id = 0L
}
