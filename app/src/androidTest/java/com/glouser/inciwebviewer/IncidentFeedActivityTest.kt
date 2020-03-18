package com.glouser.inciwebviewer

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import android.provider.BaseColumns
import android.test.mock.MockCursor
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.glouser.inciwebviewer.provider.IncidentContract
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class IncidentFeedActivityTest {

    @get:Rule
    val intentsTestRule = object : IntentsTestRule<MainActivity>(MainActivity::class.java) {
        /**
         * Override getActivityIntent() to inject TestActions for testing
         * @return the Intent that will start the Activity
         */
        override fun getActivityIntent(): Intent {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.putExtra(IncidentFeedFragment.EXTRA_ACTIONS_OBJ, TestActions())
            return intent
        }
    }

    @Test
    @Throws(Throwable::class)
    fun testClickItem() {
        // Save last update time.
        val activity = intentsTestRule.activity as MainActivity
        val prefs = activity.getPreferences(Context.MODE_PRIVATE)
        val savedLastUpdate = prefs.getLong(IncidentFeedFragment.PREF_LAST_UPDATE, 0)

        // Force onUpdateDone.
//        intentsTestRule.runOnUiThread { activity.onUpdateDone() }

        // Restore last update time.
        prefs.edit().putLong(IncidentFeedFragment.PREF_LAST_UPDATE, savedLastUpdate).apply()

        // The first item should be Test Incident 0.
        onView(allOf(withId(R.id.incident_title), withText("Test Incident 0")))
                .check(matches(isDisplayed()))

        // Stub out intent call to IncidentDetailsFragment
        intending(hasComponent(IncidentDetailsFragment::class.java.name))
                .respondWith(ActivityResult(Activity.RESULT_OK, null))

        // Click the first item in the list.
        onView(withId(R.id.incident_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition<IncidentAdapter.ViewHolder>(0, click()))

        // Verify that the details activity was launched.
        intended(allOf(
                hasComponent(IncidentDetailsFragment::class.java.name),
                hasData(IncidentContract.IncidentEntry.buildUri(0))))
        Intents.assertNoUnverifiedIntents()
    }

    /**
     * These are the mocked out actions for the IncidentFeedFragment to use during test.
     */
    private class TestActions : IncidentFeedFragment.Actions, Parcelable {

        override fun startFeedDownload() {
            // no-op
        }

        override val incidentList: Cursor?
            get() = object : MockCursor() {
                private val NUM_TEST_INCIDENTS = 5
                private val COL_ID = 0
                private val COL_NAME = 1
                private val COL_DATE = 2
                private val COL_DESC = 3

                private var mPosition: Int = 0

                private val mColumnIndex = HashMap<String, Int>()

                init {
                    mColumnIndex[BaseColumns._ID] = COL_ID
                    mColumnIndex[IncidentContract.IncidentEntry.COLUMN_NAME] = COL_NAME
                    mColumnIndex[IncidentContract.IncidentEntry.COLUMN_DATE] = COL_DATE
                    mColumnIndex[IncidentContract.IncidentEntry.COLUMN_DESCRIPTION] = COL_DESC
                }

                override fun getCount(): Int {
                    return NUM_TEST_INCIDENTS
                }

                override fun moveToPosition(position: Int): Boolean {
                    if (position < NUM_TEST_INCIDENTS) {
                        mPosition = position
                        return true
                    }
                    return false
                }

                override fun getColumnIndex(columnName: String?): Int {
                    val i = mColumnIndex[columnName]
                    return i ?: -1
                }

                override fun getLong(columnIndex: Int): Long {
                    return if (columnIndex == COL_ID) {
                        mPosition.toLong()
                    } else {
                        -1
                    }
                }

                override fun getString(columnIndex: Int): String? {
                    return when (columnIndex) {
                        COL_NAME -> "Test Incident $mPosition"
                        COL_DATE -> "date $mPosition"
                        COL_DESC -> "This is the test description for Test Incident $mPosition"
                        else -> null
                    }
                }

                override fun close() {}
            }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {}

        companion object {

            /**
             * Parcelable interface implementation. Mostly empty because we have no state to parcel.
             * CREATOR creates instances of this Parcelable.
             */
            @JvmField
            val CREATOR = object : Parcelable.Creator<TestActions> {
                override fun createFromParcel(`in`: Parcel): TestActions {
                    return TestActions()
                }

                override fun newArray(size: Int): Array<TestActions?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}
