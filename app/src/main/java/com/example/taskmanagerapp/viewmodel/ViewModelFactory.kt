package com.example.taskmanagerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanagerapp.repository.TaskRepository

class ViewModelFactory(private val repository: TaskRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(repository) as T
    }
}