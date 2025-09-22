package com.timesurvey.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.timesurvey.data.database.AppDatabase
import com.timesurvey.presentation.theme.TimeSurveyTheme

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val alarmScheduler by lazy { AlarmScheduler(this) }
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(database.timeUsageDao(), alarmScheduler)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            TimeSurveyTheme {
                SettingsScreen(viewModel, onEditCategories = {})
            }
        }
    }
}