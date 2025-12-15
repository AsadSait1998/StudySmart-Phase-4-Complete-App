package com.duotech.studysmart.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StudySmartDatabase : RoomDatabase() {
    abstract fun dao(): StudySmartDao

    companion object {
        @Volatile private var INSTANCE: StudySmartDatabase? = null

        fun getInstance(context: Context): StudySmartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudySmartDatabase::class.java,
                    "studysmart.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
