package com.timesurvey.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.timesurvey.data.database.TimeUsageDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel(
    private val dao: TimeUsageDao,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {
    val categories = dao.getAllCategories()

    private val _alarmsEnabled = MutableStateFlow(true)
    val alarmsEnabled: StateFlow<Boolean> = _alarmsEnabled.asStateFlow()

    private val _alarmInterval = MutableStateFlow(2)
    val alarmInterval: StateFlow<Int> = _alarmInterval.asStateFlow()

    init {
        alarmsEnabled.onEach { enabled ->
            if (enabled) {
                alarmScheduler.schedule(alarmInterval.value)
            } else {
                alarmScheduler.cancel()
            }
        }.launchIn(viewModelScope)

        alarmInterval.onEach { interval ->
            if (alarmsEnabled.value) {
                alarmScheduler.schedule(interval)
            }
        }.launchIn(viewModelScope)
    }

    fun setAlarmsEnabled(enabled: Boolean) {
        _alarmsEnabled.value = enabled
    }

    fun setAlarmInterval(interval: Int) {
        _alarmInterval.value = interval
    }
}

class MainViewModelFactory(
    private val dao: TimeUsageDao,
    private val alarmScheduler: AlarmScheduler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dao, alarmScheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}