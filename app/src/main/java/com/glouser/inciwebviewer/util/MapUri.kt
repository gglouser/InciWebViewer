package com.glouser.inciwebviewer.util

import android.net.Uri

/**
 * Utility functions related to Google Maps URIs.
 */

private const val BASE_URI = "https://www.google.com/maps/search/?api=1"
private const val PARAM_QUERY = "query"
private const val DEFAULT_COORD = "0.0"

/**
 * Build a Google Maps URI from coordinates.
 *
 * @param latitude  the latitude
 * @param longitude the longitude
 * @return a Google Maps URI
 */
fun buildFromCoords(latitude: String?, longitude: String?): Uri {
    val query = safeCoord(latitude) + "," + safeCoord(longitude)
    return Uri.parse(BASE_URI).buildUpon()
            .appendQueryParameter(PARAM_QUERY, query)
            .build()
}

/**
 * Replace null or empty coordinate with default value.
 *
 * @param coord a coordinate String
 * @return non-null coordinate String
 */
private fun safeCoord(coord: String?): String {
    return if (coord == null || coord.isEmpty()) DEFAULT_COORD else coord
}
