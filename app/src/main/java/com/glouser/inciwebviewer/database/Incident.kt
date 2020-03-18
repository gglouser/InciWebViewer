package com.glouser.inciwebviewer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incident_table")
data class Incident(
        @PrimaryKey(autoGenerate = true)
        var incidentID: Long = 0L,

        @ColumnInfo(name = "name")
        var name: String = "",

        @ColumnInfo(name = "date")
        var date: String = "",

        @ColumnInfo(name = "description")
        var description: String = "",

        @ColumnInfo(name = "link")
        var link: String = "",

        @ColumnInfo(name = "lat")
        var latitude: String = "",

        @ColumnInfo(name = "long")
        var longitude: String = ""
)
