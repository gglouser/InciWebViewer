package com.glouser.inciwebviewer.util

import android.text.format.DateUtils
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions related to the date format that appears in InciWeb RSS feeds.
 */

/**
 * Tag for logging.
 */
private const val TAG = "DateFormat"

/**
 * This is the date format used in the InciWeb RSS feed.
 */
private const val INCIWEB_DATE_FORMAT_STR = "EEE, dd MMM yyyy HH:mm:ss XXX"

internal val INCIWEB_DATE_FORMAT = SimpleDateFormat(INCIWEB_DATE_FORMAT_STR, Locale.US)

/**
 * Parse a date from the InciWeb RSS feed.
 *
 * @param dateStr String containing a date in the InciWeb RSS feed date format
 * @return a Date object representing that date, or null if dateStr cannot be parsed
 */
internal fun parse(dateStr: String): Date? {
    return try {
        INCIWEB_DATE_FORMAT.parse(dateStr)
    } catch (e: ParseException) {
        Log.w(TAG, "error parsing date from RSS: $dateStr", e)
        null
    }
}

/**
 * Convert a date string to a relative time string (like "1 hour ago").
 *
 * If the given date string cannot be converted into a Date, it is returned unchanged.
 *
 * @param dateStr a date in the InciWeb RSS feed date format
 * @return a CharSequence representing the relative time between the given date and now
 */
fun dateToRelative(dateStr: String): CharSequence {
    val date = parse(dateStr)
    val now = System.currentTimeMillis()
    return if (date != null)
        DateUtils.getRelativeTimeSpanString(date.time, now, DateUtils.MINUTE_IN_MILLIS)
    else
        dateStr
}
