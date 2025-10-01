package com.timesurvey.presentation

import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.timesurvey.data.database.AppDatabase
import com.timesurvey.data.database.Category
import com.timesurvey.data.database.TimeUsageRecord
import com.timesurvey.presentation.theme.TimeSurveyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "AlarmActivity"

class AlarmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        Log.d(TAG, "========== ALARM ACTIVITY CREATED ==========")
        Log.d(TAG, "Time: ${dateFormat.format(Date())}")
        Log.d(TAG, "Intent: ${intent?.toString()}")
        Log.d(TAG, "Intent extras: ${intent?.extras?.keySet()?.joinToString()}")

        // Check device lock status
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val isLocked = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            keyguardManager.isDeviceLocked
        } else {
            keyguardManager.isKeyguardLocked
        }
        Log.d(TAG, "Device locked: $isLocked")

        // Set window flags to show over lock screen and turn on screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            Log.d(TAG, "✓ setShowWhenLocked(true) and setTurnScreenOn(true) called")
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
            Log.d(TAG, "✓ Window flags set (legacy API)")
        }

        // Keep screen on while activity is visible
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        Log.d(TAG, "✓ FLAG_KEEP_SCREEN_ON set")

        // Start vibration immediately
        Log.d(TAG, "Starting vibration...")
        val vibrator = getSystemService(Vibrator::class.java)
        val vibrationEffect = VibrationEffect.createWaveform(
            longArrayOf(0, 300, 200, 300, 200, 300),
            intArrayOf(0, 255, 0, 255, 0, 255),
            -1 // Don't repeat
        )
        vibrator.vibrate(vibrationEffect)
        Log.d(TAG, "✓ Vibration triggered")

        // Handle back button press - prevent dismissal without category selection
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "Back button pressed - ignoring (user must select category)")
            }
        })
        Log.d(TAG, "✓ Back button handler registered")

        Log.d(TAG, "Setting up Compose UI...")
        setContent {
            TimeSurveyTheme {
                AlarmScreen(
                    onCategorySelected = { categoryId ->
                        Log.d(TAG, "Category selected: $categoryId")
                        recordTimeUsage(categoryId)
                        dismissAlarm()
                    }
                )
            }
        }
        Log.d(TAG, "✓ Compose UI set")
        Log.d(TAG, "========== ALARM ACTIVITY CREATED END ==========")
    }

    private fun recordTimeUsage(categoryId: Int) {
        Log.d(TAG, "========== RECORD TIME USAGE ==========")
        Log.d(TAG, "Category ID: $categoryId")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getDatabase(this@AlarmActivity).timeUsageDao()
                val record = TimeUsageRecord(
                    categoryId = categoryId,
                    timestamp = System.currentTimeMillis()
                )
                dao.insertTimeUsageRecord(record)
                Log.d(TAG, "✓ Time usage record inserted: $record")
            } catch (e: Exception) {
                Log.e(TAG, "ERROR inserting time usage record: ${e.message}", e)
            }
        }
    }

    private fun dismissAlarm() {
        Log.d(TAG, "========== DISMISS ALARM ==========")
        // Cancel any notifications
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ALARM_NOTIFICATION_ID)
        Log.d(TAG, "✓ Notification cancelled")

        // Finish the activity
        finish()
        Log.d(TAG, "✓ Activity finished")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "AlarmActivity.onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "AlarmActivity.onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "AlarmActivity.onPause()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "AlarmActivity.onDestroy()")
    }
}

@Composable
fun AlarmScreen(
    onCategorySelected: (Int) -> Unit,
    viewModel: AlarmViewModel = viewModel(
        factory = AlarmViewModelFactory(
            AppDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current).timeUsageDao()
        )
    )
) {
    val categories by viewModel.categories.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (categories.isEmpty()) {
                Text(
                    text = "No categories available",
                    textAlign = TextAlign.Center
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "What are you doing?",
                        style = MaterialTheme.typography.title2,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    ScalingLazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(categories) { category ->
                            CategoryChip(
                                category = category,
                                onClick = { onCategorySelected(category.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: Category,
    onClick: () -> Unit
) {
    Chip(
        onClick = onClick,
        label = {
            Text(
                text = category.name,
                style = MaterialTheme.typography.button,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = ChipDefaults.primaryChipColors()
    )
}