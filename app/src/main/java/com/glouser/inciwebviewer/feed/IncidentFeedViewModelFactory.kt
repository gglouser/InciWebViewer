package com.glouser.inciwebviewer.feed

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.glouser.inciwebviewer.database.IncidentDatabaseDao

class IncidentFeedViewModelFactory(
        private val database: IncidentDatabaseDao,
        private val sharedPreferences: SharedPreferences)
    : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncidentFeedViewModel::class.java)) {
            return IncidentFeedViewModel(database, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
