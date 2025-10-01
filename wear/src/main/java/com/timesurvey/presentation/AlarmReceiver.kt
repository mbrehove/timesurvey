package com.timesurvey.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "AlarmReceiver"

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        Log.d(TAG, "========== ALARM RECEIVED ==========")
        Log.d(TAG, "Time: ${dateFormat.format(Date())}")
        Log.d(TAG, "Intent action: ${intent.action}")
        Log.d(TAG, "Intent extras: ${intent.extras?.keySet()?.joinToString()}")

        try {
            // Acquire wake lock to ensure we can complete our work
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "timesurvey:alarm_wakelock"
            )
            wakeLock.acquire(60000) // 60 second timeout
            Log.d(TAG, "✓ Wake lock acquired")

            try {
                // Create the notification channel
                createNotificationChannel(context)
                Log.d(TAG, "✓ Notification channel created/verified")

                // Show the notification with full-screen intent
                showAlarmNotification(context)
                Log.d(TAG, "✓ Alarm notification shown")
            } finally {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                    Log.d(TAG, "✓ Wake lock released")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "ERROR in onReceive: ${e.message}", e)
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
        }

        // Reschedule the next alarm
        val intervalMinutes = intent.getIntExtra(EXTRA_INTERVAL_MINUTES, -1)
        Log.d(TAG, "Interval minutes from intent: $intervalMinutes")
        if (intervalMinutes != -1) {
            Log.d(TAG, "Rescheduling next alarm...")
            val scheduler = AlarmScheduler(context)
            scheduler.schedule(intervalMinutes)
            Log.d(TAG, "✓ Next alarm scheduled")
        } else {
            Log.w(TAG, "WARNING: No interval found in intent, not rescheduling")
        }

        Log.d(TAG, "========== ALARM RECEIVER END ==========")
    }
}