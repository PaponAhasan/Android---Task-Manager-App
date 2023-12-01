package com.example.taskmanagerapp.repository

import androidx.lifecycle.LiveData
import com.example.taskmanagerapp.dao.TaskDao
import com.example.taskmanagerapp.model.TaskList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun deleteAllTasks() {
        withContext(Dispatchers.IO) {
            taskDao.deleteAllTasks()
        }
    }

    suspend fun insert(task: TaskList) {
        withContext(Dispatchers.IO) {
            taskDao.insert(task)
        }
    }

    suspend fun update(task: TaskList) {
        withContext(Dispatchers.IO) {
            taskDao.update(task)
        }
    }

    suspend fun delete(task: TaskList) {
        withContext(Dispatchers.IO) {
            taskDao.delete(task)
        }
    }

    val getAllTasks: LiveData<List<TaskList>> = taskDao.getAllTasks()
}