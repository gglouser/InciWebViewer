package com.glouser.inciwebviewer.util

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class DateFormatTest {

    @Test
    fun parseTest() {
        val date = parse("Thu, 15 Aug 2019 17:11:16 -05:00")
        assertNotNull(date)
        assertEquals(1565907076000L, date?.time)
    }

    @Test
    fun dateToRelative() {
        val oneHourAgo = Date.from(Instant.now().minusSeconds((60 * 60).toLong()))
        val date = INCIWEB_DATE_FORMAT.format(oneHourAgo)
        val relativeDate = dateToRelative(date)
        assertEquals("1 hour ago", relativeDate)
    }
}
