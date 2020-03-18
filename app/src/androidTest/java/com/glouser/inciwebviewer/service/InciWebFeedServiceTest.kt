package com.glouser.inciwebviewer.service

import android.content.ContentValues
import android.net.Uri
import android.test.mock.MockContentProvider
import android.test.mock.MockContentResolver
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.glouser.inciwebviewer.provider.IncidentContract
import com.glouser.inciwebviewer.test.R
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Tests for InciWebFeedService logic.
 *
 *
 * InciWebFeedService is an IntentService, which presents testing challenges.
 * The Android developer guide suggests putting the service logic in another
 * class that can be unit tested. In this case, the service logic is mostly in
 * IncidentFeedHandler.
 */
@RunWith(AndroidJUnit4::class)
class InciWebFeedServiceTest {

    /**
     * Mocked ContentResolver and ContentProvider used to stand in for incident database.
     */
    private var mResolver: MockContentResolver? = null
    private var mProvider: TestContentProvider? = null

    /**
     * Create the mock objects for the tests.
     */
    @Before
    fun setUp() {
        mProvider = TestContentProvider()
        mResolver = MockContentResolver()
        mResolver!!.addProvider(IncidentContract.CONTENT_AUTHORITY, mProvider)
    }

    /**
     * Test normal usage of IncidentFeedHandler.
     *
     * @throws IOException if there is an IO problem
     */
    @Test
    @Throws(IOException::class)
    fun handleFeed() {
        // Get sample RSS from a resource file.
        val inputStream = InstrumentationRegistry.getInstrumentation()
                .context.resources
                .openRawResource(R.raw.inciweb_sample)

        val handler = IncidentFeedHandler(mResolver!!, database)
        handler.fetchFeed(inputStream)

        assertEquals(69, mProvider!!.insertCount.toLong())
    }

    /**
     * A mock ContentProvider to handle incident DB operations during tests.
     */
    private inner class TestContentProvider : MockContentProvider() {
        var insertCount = 0
            private set

        override fun insert(uri: Uri, values: ContentValues?): Uri? {
            assertEquals(IncidentContract.IncidentEntry.CONTENT_URI, uri)
            insertCount += 1
            return IncidentContract.IncidentEntry.buildUri(insertCount.toLong())
        }

        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
            return 0
        }
    }
}
