package com.timesurvey.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TimeUsageDao {
    @Insert
    suspend fun insertTimeUsageRecord(record: TimeUsageRecord)

    @Query("SELECT * FROM time_usage_records ORDER BY timestamp DESC")
    suspend fun getAllTimeUsageRecords(): List<TimeUsageRecord>

    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories ORDER BY id ASC")
    suspend fun getAllCategories(): List<Category>
}
