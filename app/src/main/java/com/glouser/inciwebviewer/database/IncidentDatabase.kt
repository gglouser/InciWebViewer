package com.glouser.inciwebviewer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Incident::class], version = 1, exportSchema = false)
abstract class IncidentDatabase : RoomDatabase() {

    abstract val incidentDatabaseDao: IncidentDatabaseDao

    companion object {

        private const val DATABASE_NAME = "incident_database"

        @Volatile
        private var INSTANCE: IncidentDatabase? = null

        fun getInstance(context: Context): IncidentDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(context, IncidentDatabase::class.java, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
