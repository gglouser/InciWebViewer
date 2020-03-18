package com.glouser.inciwebviewer.details

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.glouser.inciwebviewer.database.Incident
import com.glouser.inciwebviewer.database.IncidentDatabaseDao
import com.glouser.inciwebviewer.util.buildFromCoords

class IncidentDetailsViewModel(
        incidentKey: Long,
        database: IncidentDatabaseDao) : ViewModel() {

    companion object {
        val TAG: String = IncidentDetailsViewModel::class.java.simpleName
    }

    val incident: LiveData<Incident> = database.get(incidentKey)

    private val _viewUriEvent = MutableLiveData<Uri>()
    val viewUriEvent: LiveData<Uri>
        get() = _viewUriEvent

    fun doneViewUri() {
        _viewUriEvent.value = null
    }

    fun onGeoClick() {
        val incident = incident.value ?: return
        val mapUri = buildFromCoords(incident.latitude, incident.longitude)
        Log.v(TAG, "viewing map URI: $mapUri")
        _viewUriEvent.value = mapUri
    }

    fun onLinkClick() {
        val incident = incident.value ?: return
        Log.v(TAG, "viewing incident link: ${incident.link}")
        _viewUriEvent.value = Uri.parse(incident.link)
    }
}
