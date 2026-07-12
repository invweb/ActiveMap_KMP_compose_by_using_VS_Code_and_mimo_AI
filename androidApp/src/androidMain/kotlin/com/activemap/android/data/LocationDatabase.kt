package com.activemap.android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [LocationEntity::class, TrackEntity::class, TrackPointEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun trackDao(): TrackDao
    abstract fun trackPointDao(): TrackPointDao
    
    companion object {
        @Volatile
        private var INSTANCE: LocationDatabase? = null
        
        fun getDatabase(context: Context): LocationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocationDatabase::class.java,
                    "activemap_database"
                )
                .fallbackToDestructiveMigration() // For simplicity during development
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}