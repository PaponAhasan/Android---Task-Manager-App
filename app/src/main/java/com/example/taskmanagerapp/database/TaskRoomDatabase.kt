package com.example.taskmanagerapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taskmanagerapp.dao.TaskDao
import com.example.taskmanagerapp.model.TaskList

@Database(entities = [TaskList::class], version = 1, exportSchema = false)
abstract class TaskRoomDatabase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao

    companion object {
        // Singleton prevents multiple instances of database
        @Volatile
        private var INSTANCE: TaskRoomDatabase? = null

        fun getDatabase(context: Context): TaskRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskRoomDatabase::class.java,
                    "task_database"
                ).build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}