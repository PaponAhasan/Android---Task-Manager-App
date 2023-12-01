package com.example.taskmanagerapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagerapp.model.TaskList
import com.example.taskmanagerapp.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    fun delete(task: TaskList) = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.delete(task)
    }

    fun deleteAllTasks() = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.deleteAllTasks()
    }

    fun update(task: TaskList) = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.update(task)
    }

    fun insert(task: TaskList) = viewModelScope.launch(Dispatchers.IO) {
        taskRepository.insert(task)
    }

    fun getAllTasks(): LiveData<List<TaskList>> = taskRepository.getAllTasks
}