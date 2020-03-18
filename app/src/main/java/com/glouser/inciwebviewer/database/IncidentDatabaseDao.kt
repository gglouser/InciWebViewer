package com.glouser.inciwebviewer.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface IncidentDatabaseDao {

    @Insert
    fun insert(incident: Incident)

    @Update
    fun update(incident: Incident)

    @Query("SELECT * FROM incident_table WHERE incidentID = :key")
    fun get(key: Long): LiveData<Incident>

    @Query("SELECT * FROM incident_table ORDER BY incidentID")
    fun getAll(): LiveData<List<Incident>>

    @Query("DELETE FROM incident_table")
    fun clear()
}
