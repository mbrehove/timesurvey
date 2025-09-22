package com.timesurvey.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeUsageDao {
    @Query("SELECT * FROM categories ORDER BY `order` ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories ORDER BY `order` ASC")
    suspend fun getAllCategoriesSuspend(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Insert
    suspend fun insertTimeUsageRecord(record: TimeUsageRecord)

    @Query("SELECT * FROM time_usage_records WHERE timestamp BETWEEN :startTime AND :endTime")
    fun getTimeUsageRecords(startTime: Long, endTime: Long): Flow<List<TimeUsageRecord>>
}