package com.timesurvey.presentation

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.timesurvey.data.database.AppDatabase
import com.timesurvey.data.database.TimeUsageRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val categoryId = intent.getIntExtra("category_id", -1)
        if (categoryId != -1) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dao = AppDatabase.getDatabase(context).timeUsageDao()
                    dao.insertTimeUsageRecord(
                        TimeUsageRecord(
                            categoryId = categoryId,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                } finally {
                    pendingResult.finish()
                }
            }
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }
}