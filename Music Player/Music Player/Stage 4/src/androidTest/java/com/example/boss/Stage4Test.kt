package com.example.boss

import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withChild
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Stage4Test: AbstractUnitTest<MainActivity>(MainActivity::class.java) {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun before() {
        activity = activityRule.activity
        println("before")
    }

    @Test
    fun checkIsSearchButtonExist() {
        val message = "Is search_button exists?"
        assertNotNull(message, find(R.id.search_button))
    }

    @Test
    fun checkAreThreeStatusButtonsExist() {
        val message = "Are all three song fragments displayed on screen after searching?"
        onView(withId(R.id.search_button)).perform(click())
        for(i in 0..2) {
            assertNotNull(
                message,
                find<LinearLayout>(R.id.song_list).getChildAt(i)
                    .findViewById<ImageButton>(R.id.song_status_button)
            )
        }
    }

    @Test
    fun checkIsCurrentTimeViewExist() {
        val message = "Is current_time_view exists?"
        assertNotNull(message, find(R.id.current_time_view))
    }

    @Test
    fun checkIsStatusButtonRunMusic() {
        val message = "Is click on song_status_button run music playback?"
        val message2 = "Is current_time_view changes text while playback?"
        onView(withId(R.id.search_button)).perform(click())
        for(i in 0..2) {
//            val status = find<LinearLayout>(R.id.song_list).getChildAt(i)
//                .findViewById<ImageButton>(R.id.song_status_button)
//            status.performClick()

            Thread.sleep(3000)
            onData(withId(R.id.song_list)).atPosition(i).onChildView(withId(R.id.song_status_button)).perform(
                click())
//            status.performClick()
            assertEquals(message2, true, find<TextView>(R.id.current_time_view).text.toString()[4].toInt() in 1..3)
        }
    }
}