package com.timesurvey.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.timesurvey.data.database.AppDatabase
import com.timesurvey.presentation.theme.TimeSurveyTheme

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(database.timeUsageDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            TimeSurveyTheme {
                val categories = viewModel.categories.collectAsStateWithLifecycle(initialValue = emptyList())
                CategoryList(categories = categories.value)
            }
        }
    }
}