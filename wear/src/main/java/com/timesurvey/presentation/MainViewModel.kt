package com.timesurvey.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.timesurvey.data.database.TimeUsageDao

class MainViewModel(private val dao: TimeUsageDao) : ViewModel() {
    val categories = dao.getAllCategories()
}

class MainViewModelFactory(private val dao: TimeUsageDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}