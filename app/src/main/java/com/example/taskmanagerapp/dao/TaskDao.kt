package com.example.taskmanagerapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.taskmanagerapp.model.TaskList

@Dao
interface TaskDao {

    @Query("DELETE FROM tasks_table")
    suspend fun deleteAllTasks()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(note: TaskList)

    @Update
    fun update(note: TaskList)

    @Delete
    fun delete(note: TaskList)

    @Query("Select * from tasks_table order by id DESC")
    fun getAllTasks(): LiveData<List<TaskList>>

    @Query(
        "UPDATE tasks_table SET titleText = :titleText, bodyText = :bodyText, eventText = :eventText, " +
                "dateText = :dateText, timeText = :timeText WHERE id = :id"
    )
    fun updateExistingTasks(
        id: Long, titleText: String, bodyText: String, eventText: String, dateText: String,
        timeText: String
    )
}