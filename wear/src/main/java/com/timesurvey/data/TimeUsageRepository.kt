package com.timesurvey.data

import com.timesurvey.data.database.Category
import com.timesurvey.data.database.TimeUsageDao
import com.timesurvey.data.database.TimeUsageRecord
import kotlinx.coroutines.flow.Flow

class TimeUsageRepository(private val timeUsageDao: TimeUsageDao) {

    suspend fun addTimeUsageRecord(record: TimeUsageRecord) {
        timeUsageDao.insertTimeUsageRecord(record)
    }

    fun getTimeUsageRecords(startTime: Long, endTime: Long): Flow<List<TimeUsageRecord>> {
        return timeUsageDao.getTimeUsageRecords(startTime, endTime)
    }

    suspend fun addCategory(category: Category) {
        timeUsageDao.insertCategory(category)
    }

    fun getAllCategories(): Flow<List<Category>> {
        return timeUsageDao.getAllCategories()
    }
}
