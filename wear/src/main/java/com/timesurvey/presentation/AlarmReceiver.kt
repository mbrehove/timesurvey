package com.timesurvey.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.timesurvey.R
import com.timesurvey.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getDatabase(context).timeUsageDao()
                val categories = dao.getAllCategoriesSuspend()

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val channel = NotificationChannel(
                    "time_survey_channel",
                    "Time Survey",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)

                val notificationBuilder = NotificationCompat.Builder(context, "time_survey_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Time Survey")
                    .setContentText("What are you doing?")

                categories.forEach { category ->
                    val categoryIntent = Intent(context, CategoryActionReceiver::class.java).apply {
                        putExtra("category_id", category.id)
                    }
                    val pendingCategoryIntent = PendingIntent.getBroadcast(
                        context,
                        category.id,
                        categoryIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    notificationBuilder.addAction(
                        NotificationCompat.Action(
                            null,
                            category.name,
                            pendingCategoryIntent
                        )
                    )
                }

                notificationManager.notify(1, notificationBuilder.build())
            } finally {
                pendingResult.finish()
            }
        }
    }
}