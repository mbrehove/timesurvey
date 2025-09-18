package com.timesurvey.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timesurvey.data.TimeUsageRepository
import com.timesurvey.data.database.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: TimeUsageRepository) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _alarmsOn = MutableStateFlow(false)
    val alarmsOn = _alarmsOn.asStateFlow()

    private val _alarmInterval = MutableStateFlow(60) // Default to 60 minutes
    val alarmInterval = _alarmInterval.asStateFlow()

    init {
        viewModelScope.launch {
            _categories.value = repository.getAllCategories()
        }
    }

    fun toggleAlarms(isOn: Boolean) {
        _alarmsOn.value = isOn
    }

    fun setAlarmInterval(interval: Int) {
        _alarmInterval.value = interval
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.addCategory(Category(name = name))
            _categories.value = repository.getAllCategories()
        }
    }
}
