package com.timesurvey.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val EXTRA_INTERVAL_MINUTES = "com.timesurvey.presentation.INTERVAL_MINUTES"
private const val TAG = "AlarmScheduler"

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(intervalMinutes: Int) {
        Log.d(TAG, "========== SCHEDULE ALARM START ==========")
        Log.d(TAG, "Interval minutes: $intervalMinutes")

        // Create the alarm trigger intent
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_INTERVAL_MINUTES, intervalMinutes)
        }
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the "show settings" intent for AlarmClockInfo
        val showIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val showPendingIntent = PendingIntent.getActivity(
            context,
            1,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = System.currentTimeMillis() + intervalMinutes * 60 * 1000
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        Log.d(TAG, "Current time: ${dateFormat.format(Date(System.currentTimeMillis()))}")
        Log.d(TAG, "Alarm scheduled for: ${dateFormat.format(Date(triggerAtMillis))}")
        Log.d(TAG, "Time until alarm: ${(triggerAtMillis - System.currentTimeMillis()) / 1000} seconds")

        // Check if we can schedule exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val canScheduleExact = alarmManager.canScheduleExactAlarms()
            Log.d(TAG, "Can schedule exact alarms: $canScheduleExact")

            if (!canScheduleExact) {
                Log.e(TAG, "ERROR: Cannot schedule exact alarms! Need SCHEDULE_EXACT_ALARM permission")
                Log.e(TAG, "User must grant this permission in system settings")
                // Fall back to regular alarm
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    alarmPendingIntent
                )
                Log.w(TAG, "Falling back to regular alarm.set()")
                return
            }
        }

        // Use setAlarmClock for precise, user-facing alarm
        Log.d(TAG, "Using setAlarmClock() for precise alarm")
        val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, showPendingIntent)

        try {
            alarmManager.setAlarmClock(alarmClockInfo, alarmPendingIntent)
            Log.d(TAG, "✓ setAlarmClock() called successfully")
            Log.d(TAG, "✓ Alarm clock icon should appear in status bar")
            Log.d(TAG, "✓ System will wake device before alarm fires")
        } catch (e: Exception) {
            Log.e(TAG, "ERROR scheduling alarm: ${e.message}", e)
        }

        Log.d(TAG, "========== SCHEDULE ALARM END ==========")
    }

    fun cancel() {
        Log.d(TAG, "========== CANCEL ALARM START ==========")
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "✓ Alarm cancelled")
        Log.d(TAG, "========== CANCEL ALARM END ==========")
    }

    fun triggerAlarmNow() {
        Log.d(TAG, "========== TRIGGER ALARM NOW (TEST) ==========")
        val receiver = AlarmReceiver()
        val intent = Intent(context, AlarmReceiver::class.java)
        receiver.onReceive(context, intent)
        Log.d(TAG, "✓ Test alarm triggered")
    }
}