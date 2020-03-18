package com.glouser.inciwebviewer

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.glouser.inciwebviewer.provider.Incident
import com.glouser.inciwebviewer.provider.IncidentContract
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IncidentDetailsActivityTest {

    @get:Rule
    val testRule = object : IntentsTestRule<MainActivity>(MainActivity::class.java) {

        /**
         * Override getActivityIntent() to inject TestActions for testing
         *
         * @return the Intent that will start the Activity
         */
        override fun getActivityIntent(): Intent {
            val intent = Intent(ACTION_VIEW, IncidentContract.IncidentEntry.buildUri(1))
            intent.putExtra(MainActivity.EXTRA_ACTIONS_OBJ, TestActions())
            return intent
        }
    }

    @Test
    fun basicIncident() {
        onView(withId(R.id.details_title)).check(matches(isDisplayed()))
                .check(matches(withText("test incident")))
        onView(withId(R.id.details_date)).check(matches(isDisplayed()))
                .check(matches(withText("test date")))
        onView(withId(R.id.details_geo)).check(matches(isDisplayed()))
                .check(matches(withText("45.000, -90.000")))
        onView(withId(R.id.details_description)).check(matches(isDisplayed()))
                .check(matches(withText("test description")))
    }

    @Test
    fun viewInBrowserClick() {
        // Stub out intent that launches web browser.
        intending(hasAction(ACTION_VIEW))
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        // Click on the view link.
        onView(withId(R.id.details_link)).perform(click())

        // Verify that the view intent was sent.
        intended(allOf(
                hasAction(ACTION_VIEW),
                hasData("test link")))
        Intents.assertNoUnverifiedIntents()
    }

    @Test
    fun viewMapLocationClick() {
        // Stub out intent that launches web browser.
        intending(hasAction(ACTION_VIEW))
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        // Click on the view link.
        onView(withId(R.id.details_geo)).perform(click())

        // Verify that the view intent was sent.
        intended(allOf(
                hasAction(ACTION_VIEW),
                hasData("https://www.google.com/maps/search/?api=1&query=45.000%2C-90.000")))
        Intents.assertNoUnverifiedIntents()
    }

    /**
     * These are the mocked out actions for the IncidentFeedFragment to use during test.
     */
    internal class TestActions : MainActivity.Actions, Parcelable {

        override fun getIncident(uri: Uri): Incident {
            val incident = Incident()
            incident.name = "test incident"
            incident.date = "test date"
            incident.description = "test description"
            incident.link = "test link"
            incident.latitude = "45.000"
            incident.longitude = "-90.000"
            return incident
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(parcel: Parcel, i: Int) {}

        companion object {

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
