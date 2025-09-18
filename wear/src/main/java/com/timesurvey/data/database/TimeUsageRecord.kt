package com.timesurvey.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_usage_records")
data class TimeUsageRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val category: String
)
