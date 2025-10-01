package com.timesurvey.presentation

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.timesurvey.data.database.AppDatabase
import com.timesurvey.presentation.theme.TimeSurveyTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val alarmScheduler by lazy { AlarmScheduler(this) }
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(database.timeUsageDao(), alarmScheduler)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "========== MAIN ACTIVITY CREATED ==========")

        // Check and request SCHEDULE_EXACT_ALARM permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w(TAG, "SCHEDULE_EXACT_ALARM permission not granted")
                Log.d(TAG, "Opening system settings to grant permission...")

                // Open system settings to allow exact alarms
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
                Log.d(TAG, "Settings activity launched")
            } else {
                Log.d(TAG, "✓ SCHEDULE_EXACT_ALARM permission already granted")
            }
        } else {
            Log.d(TAG, "Android < 12, no SCHEDULE_EXACT_ALARM permission needed")
        }

        installSplashScreen()
        setContent {
            TimeSurveyTheme {
                SettingsScreen(viewModel, onEditCategories = {})
            }
        }

        Log.d(TAG, "========== MAIN ACTIVITY CREATED END ==========")
    }
}