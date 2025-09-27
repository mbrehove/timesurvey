package com.timesurvey.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

const val EXTRA_INTERVAL_MINUTES = "com.timesurvey.presentation.INTERVAL_MINUTES"

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(intervalMinutes: Int) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_INTERVAL_MINUTES, intervalMinutes)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = System.currentTimeMillis() + intervalMinutes * 60 * 1000
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // App cannot schedule exact alarms. Maybe show a dialog to the user to grant the permission.
            // For now, we will just schedule a normal alarm.
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun cancel() {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun triggerAlarmNow() {
        // For testing purposes, we can simulate the receiver's behavior
        val receiver = AlarmReceiver()
        val intent = Intent(context, AlarmReceiver::class.java)
        receiver.onReceive(context, intent)
    }
}