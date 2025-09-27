package com.timesurvey.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Create the notification channel
        createNotificationChannel(context)
        // Show the notification
        showAlarmNotification(context)

        // Reschedule the next alarm
        val intervalMinutes = intent.getIntExtra(EXTRA_INTERVAL_MINUTES, -1)
        if (intervalMinutes != -1) {
            val scheduler = AlarmScheduler(context)
            scheduler.schedule(intervalMinutes)
        }
    }
}