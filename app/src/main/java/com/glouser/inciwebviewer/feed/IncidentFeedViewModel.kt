package com.glouser.inciwebviewer.feed

import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.glouser.inciwebviewer.database.IncidentDatabaseDao
import com.glouser.inciwebviewer.service.IncidentFeedHandler
import kotlinx.coroutines.*
import java.net.URL

enum class FeedApiStatus {
    LOADING,
    DONE,
    ERROR
}

/**
 * ViewModel for the incident feed Fragment.
 */
class IncidentFeedViewModel(
        private val database: IncidentDatabaseDao,
        private val sharedPreferences: SharedPreferences)
    : ViewModel() {

    companion object {
        /** Tag for logging */
        private const val TAG = "IncidentFeedViewModel"

        /**
         * URL for InciWeb incident feed.
         */
        private const val INCIWEB_INCIDENT_FEED_URL = "https://inciweb.nwcg.gov/feeds/rss/incidents/"

        /**
         * Preference key for tracking the last time we updated the RSS feed.
         */
        private const val PREF_LAST_UPDATE = "last_update_time"

        /**
         * Time interval for RSS updates. We check for updates at most once per hour.
         */
        private const val UPDATE_CHECK_TIME_MILLIS = (60 * 60 * 1000).toLong() // 1 hour
    }

    // Job and scope for coroutine management.
    private val job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.Main)

    /**
     * List of current incidents as LiveData.
     */
    val incidents = database.getAll()

    /**
     * LiveData event to signal when to navigate to incident detail view.
     */
    private val _navToIncidentDetails = MutableLiveData<Long>()
    val navToIncidentDetails: LiveData<Long>
        get() = _navToIncidentDetails

    /**
     * Reset the [navToIncidentDetails] event.
     */
    fun doneNavToIncidentDetails() {
        _navToIncidentDetails.value = null
    }

    /**
     * Handle an incident click event.
     */
    fun onIncidentClicked(incidentId: Long) {
        if (apiStatus.value != FeedApiStatus.LOADING) {
            _navToIncidentDetails.value = incidentId
        }
    }

    /**
     * Status of the feed download.
     */
    private val apiStatus = MutableLiveData<FeedApiStatus>()

    init {
        apiStatus.value = FeedApiStatus.DONE
    }

    /**
     * Visibility of spinner/progress bar.
     */
    val downloadInProgress = Transformations.map(apiStatus) {
        when (it) {
            FeedApiStatus.LOADING -> View.VISIBLE
            else -> View.GONE
        }
    }

    /**
     * Download the incident feed.
     */
    fun downloadIncidentFeed() {
        coroutineScope.launch {
            Log.d(TAG, "beginning incident feed download")
            apiStatus.value = FeedApiStatus.LOADING
            try {
                withContext(Dispatchers.IO) {
                    URL(INCIWEB_INCIDENT_FEED_URL).openStream().use { inputStream ->
                        IncidentFeedHandler(database).fetchFeed(inputStream)
                    }
                }
                apiStatus.value = FeedApiStatus.DONE
                Log.d(TAG, "incident feed download complete")

                // Update the last uptime time in prefs
                sharedPreferences.edit()
                        .putLong(PREF_LAST_UPDATE, System.currentTimeMillis())
                        .apply()

            } catch (e: Exception) {
                Log.e(TAG, "failed to download incident feed", e)
                apiStatus.value = FeedApiStatus.ERROR
            }
        }
    }

    /**
     * Update the incident feed if the last check was a while ago.
     */
    fun checkIncidentFeed() {
        val lastUpdate = sharedPreferences.getLong(PREF_LAST_UPDATE, 0)
        val now = System.currentTimeMillis()
        Log.d(TAG, "last updated " + (now - lastUpdate) / 1000 + " seconds ago")
        if (now - lastUpdate >= UPDATE_CHECK_TIME_MILLIS) {
            downloadIncidentFeed()
        }
    }

    /**
     * Handle cleanup when this ViewModel's lifecycle ends.
     */
    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
