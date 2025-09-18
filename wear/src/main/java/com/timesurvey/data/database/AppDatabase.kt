package com.timesurvey.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimeUsageRecord::class, Category::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timeUsageDao(): TimeUsageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "time_survey_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
