package com.glouser.inciwebviewer.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.glouser.inciwebviewer.database.IncidentDatabaseDao

class IncidentDetailsViewModelFactory(
        private val incidentKey: Long,
        private val database: IncidentDatabaseDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncidentDetailsViewModel::class.java)) {
            return IncidentDetailsViewModel(incidentKey, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
