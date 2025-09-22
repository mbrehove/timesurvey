package com.timesurvey.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.Stepper
import androidx.wear.compose.material.StepperDefaults
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import com.timesurvey.R

@Composable
fun SettingsScreen(viewModel: MainViewModel, onEditCategories: () -> Unit) {
    val alarmsEnabled by viewModel.alarmsEnabled.collectAsStateWithLifecycle()
    val alarmInterval by viewModel.alarmInterval.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ToggleChip(
            checked = alarmsEnabled,
            onCheckedChange = { viewModel.setAlarmsEnabled(it) },
            label = { Text(stringResource(R.string.alarms_enabled)) },
            toggleControl = {
                Switch(
                    checked = alarmsEnabled
                )
            }
        )
        Stepper(
            value = alarmInterval,
            onValueChange = { viewModel.setAlarmInterval(it) },
            valueProgression = 1..120,
            increaseIcon = { StepperDefaults.Increase },
            decreaseIcon = { StepperDefaults.Decrease }
        ) {
            Text(stringResource(R.string.alarm_interval_minutes, alarmInterval))
        }
        Chip(
            onClick = onEditCategories,
            label = { Text(stringResource(R.string.edit_categories)) }
        )
    }
}