package com.timesurvey.data

import com.timesurvey.data.database.Category
import com.timesurvey.data.database.TimeUsageDao
import com.timesurvey.data.database.TimeUsageRecord

class TimeUsageRepository(private val timeUsageDao: TimeUsageDao) {

    suspend fun addTimeUsageRecord(record: TimeUsageRecord) {
        timeUsageDao.insertTimeUsageRecord(record)
    }

    suspend fun getAllTimeUsageRecords(): List<TimeUsageRecord> {
        return timeUsageDao.getAllTimeUsageRecords()
    }

    suspend fun addCategory(category: Category) {
        timeUsageDao.insertCategory(category)
    }

    suspend fun getAllCategories(): List<Category> {
        return timeUsageDao.getAllCategories()
    }
}
