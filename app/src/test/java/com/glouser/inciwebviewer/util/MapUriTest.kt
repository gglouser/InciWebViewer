package com.glouser.inciwebviewer.util

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MapUriTest {

    @Test
    fun buildFromCoordinates() {
        val uri = buildFromCoords("-12.3456", "98.7654")
        assertNotNull(uri)
        assertEquals("https://www.google.com/maps/search/?api=1&query=-12.3456%2C98.7654",
                uri.toString())
    }

    @Test
    fun buildFromNull() {
        val uri = buildFromCoords(null, null)
        assertNotNull(uri)
        assertEquals("https://www.google.com/maps/search/?api=1&query=0.0%2C0.0",
                uri.toString())
    }
}
