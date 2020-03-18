package com.glouser.inciwebviewer.service

import android.util.Log
import com.glouser.inciwebviewer.database.Incident
import com.glouser.inciwebviewer.database.IncidentDatabaseDao
import org.xmlpull.v1.XmlPullParserException
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream

/**
 * Parses an RSS feed containing Incidents and stores them using a ContentProvider.
 *
 * This is where most of the work for InciWebFeedService happens. The implementation
 * is in this class to facilitate testing, as suggested in Google testing guidelines
 * for an IntentService like InciWebFeedService.
 *
 * @param database
 */
internal class IncidentFeedHandler(private val database: IncidentDatabaseDao) {

    companion object {
        /** Tag for logging. */
        private const val TAG = "IncidentFeedHandler"
    }

    /**
     * Fetch a list of incidents from an RSS feed.
     *
     * @param inputStream InputStream of RSS feed.
     * @throws IOException on failure to read from stream
     */
    @Throws(IOException::class)
    fun fetchFeed(inputStream: InputStream) {
        Log.d(TAG, "fetchFeed()")

        // Check whether this thread was interrupted.
        if (Thread.currentThread().isInterrupted) {
            Log.d(TAG, "download thread interrupted before download")
            return
        }

        // Download incident RSS feed
        try {
            val incidents = downloadFeed(inputStream)
            Log.v(TAG, "downloaded ${incidents.size} incidents")

            // Check again for thread interrupted.
            if (Thread.currentThread().isInterrupted) {
                Log.d(TAG, "download thread interrupted after download")
                return
            }

            // Update DB
            updateIncidentDB(incidents)

        } catch (e: XmlPullParserException) {
            Log.w(TAG, "InciWeb RSS parse error", e)
        }

    }

    /**
     * Download list of incidents from the given RSS feed.
     *
     * @param inputStream InputStream of RSS feed
     * @return list of incidents
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun downloadFeed(inputStream: InputStream): List<Incident> {
        val input = BufferedInputStream(inputStream)
        val parser = IncidentFeedParser()
        return parser.parse(input)
    }

    /**
     * Update the incidents database with a list of incidents.
     *
     * @param incidents the incidents to add to the database
     */
    private fun updateIncidentDB(incidents: List<Incident>) {
        // Delete all existing incidents
        Log.d(TAG, "deleting existing incidents from DB")
        database.clear()

        // Add the new incidents
        Log.d(TAG, "adding new incidents")
        for (incident in incidents) {
            insertIncident(incident)

            Log.v(TAG, "incident: ${incident.name} -- ${incident.date}")
        }
    }

    /**
     * Insert one incident into the DB.
     *
     * @param incident the incident to insert
     */
    private fun insertIncident(incident: Incident) {
        database.insert(incident)
    }
}
