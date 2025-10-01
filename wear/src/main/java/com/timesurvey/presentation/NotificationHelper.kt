package com.timesurvey.presentation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat

const val ALARM_CHANNEL_ID = "alarm_channel"
const val ALARM_NOTIFICATION_ID = 1
private const val TAG = "NotificationHelper"

fun createNotificationChannel(context: Context) {
    Log.d(TAG, "========== CREATE NOTIFICATION CHANNEL ==========")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Time Survey Alarms"
        val descriptionText = "Alarm clock notifications for time tracking"
        val importance = NotificationManager.IMPORTANCE_HIGH
        Log.d(TAG, "Channel name: $name")
        Log.d(TAG, "Channel importance: $importance")

        val channel = NotificationChannel(ALARM_CHANNEL_ID, name, importance).apply {
            description = descriptionText
            // Enable vibration and sound - critical for alarm clock apps
            enableVibration(true)
            enableLights(true)
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
            setBypassDnd(true) // Allow through Do Not Disturb
            setShowBadge(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        Log.d(TAG, "✓ Alarm notification channel created")
        Log.d(TAG, "  - Vibration: enabled")
        Log.d(TAG, "  - Sound: ${RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)}")
        Log.d(TAG, "  - Bypass DND: true")
    } else {
        Log.d(TAG, "API < 26, no channel needed")
    }
    Log.d(TAG, "========== CREATE NOTIFICATION CHANNEL END ==========")
}

fun showAlarmNotification(context: Context) {
    Log.d(TAG, "========== SHOW ALARM NOTIFICATION ==========")
    try {
        // STEP 1: Trigger strong vibration immediately
        Log.d(TAG, "Triggering alarm vibration...")
        triggerAlarmVibration(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Check if we can use full-screen intents (Android 14+)
        val canUseFullScreenIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val canUse = notificationManager.canUseFullScreenIntent()
            Log.d(TAG, "Android 14+ full-screen intent permission check: $canUse")
            canUse
        } else {
            Log.d(TAG, "Pre-Android 14, full-screen intents allowed by default")
            true
        }

        // STEP 2: Create the full-screen intent to launch AlarmActivity
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        }
        Log.d(TAG, "Full-screen intent created for AlarmActivity")
        Log.d(TAG, "Intent flags: NEW_TASK | CLEAR_TOP | SINGLE_TOP | EXCLUDE_FROM_RECENTS")

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d(TAG, "PendingIntent created")

        // STEP 3: Build high-priority alarm notification
        Log.d(TAG, "Building notification...")
        val builder = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Time Survey")
            .setContentText("What are you doing?")
            .setPriority(NotificationCompat.PRIORITY_MAX) // Use MAX for alarm clocks
            .setCategory(NotificationCompat.CATEGORY_ALARM) // Mark as alarm
            .setContentIntent(fullScreenPendingIntent)
            .setAutoCancel(false) // Don't dismiss until user selects category
            .setOngoing(true) // Make it persistent
            .setOnlyAlertOnce(false) // Allow repeated alerts
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Show on lock screen

        Log.d(TAG, "Notification priority: MAX")
        Log.d(TAG, "Notification category: ALARM")
        Log.d(TAG, "Auto cancel: false")
        Log.d(TAG, "Ongoing: true")

        // STEP 4: Add full-screen intent - critical for showing activity over lock screen
        if (canUseFullScreenIntent) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
            Log.d(TAG, "✓ Full-screen intent ADDED to notification (highPriority=true)")
            Log.d(TAG, "✓ AlarmActivity should launch automatically when notification fires")
        } else {
            Log.e(TAG, "ERROR: Full-screen intent permission DENIED")
            Log.e(TAG, "User must grant USE_FULL_SCREEN_INTENT permission")
            Log.w(TAG, "Notification will show but activity won't auto-launch")
        }

        // STEP 5: Post the notification
        notificationManager.notify(ALARM_NOTIFICATION_ID, builder.build())
        Log.d(TAG, "✓ Notification posted with ID: $ALARM_NOTIFICATION_ID")
        Log.d(TAG, "✓ Channel will handle sound/vibration")
        Log.d(TAG, "✓ Full-screen intent should trigger AlarmActivity")

    } catch (e: Exception) {
        Log.e(TAG, "ERROR showing notification: ${e.message}", e)
        Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
    }

    Log.d(TAG, "========== SHOW ALARM NOTIFICATION END ==========")
}

fun triggerAlarmVibration(context: Context) {
    Log.d(TAG, "--- Triggering vibration ---")
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(Vibrator::class.java)
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (!vibrator.hasVibrator()) {
            Log.w(TAG, "WARNING: Device has no vibrator")
            return
        }

        // Create strong alarm vibration pattern
        val pattern = longArrayOf(0, 500, 200, 500, 200, 500, 200, 500)
        val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255) // Max intensity
        Log.d(TAG, "Vibration pattern: ${pattern.contentToString()}")
        Log.d(TAG, "Vibration amplitudes: ${amplitudes.contentToString()}")

        val vibrationEffect = VibrationEffect.createWaveform(pattern, amplitudes, -1)
        vibrator.vibrate(vibrationEffect)
        Log.d(TAG, "✓ Vibration triggered")
    } catch (e: Exception) {
        Log.e(TAG, "ERROR triggering vibration: ${e.message}", e)
    }
}