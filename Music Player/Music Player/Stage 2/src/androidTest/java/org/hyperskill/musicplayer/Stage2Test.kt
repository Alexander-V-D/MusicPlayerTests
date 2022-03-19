package org.hyperskill.musicplayer

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Stage2Test: AbstractUnitTest<MainActivity>(MainActivity::class.java) {

    @Rule @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun before() {
        activity = activityRule.activity
        println("before")
    }

    @Test
    fun checkIsTotalTimeViewExist() {
        val message = "Is total_time_view visible for user?"
        assertNotNull(message, find(R.id.total_time_view))
    }

    @Test
    fun checkIsCurrentTimeViewExist() {
        val message = "Is current_time_view visible for user?"
        assertNotNull(message, find(R.id.current_time_view))
    }

    @Test
    fun checkIsSeekbarExist() {
        val message = "Is seekbar visible for user?"
        assertNotNull(message, find(R.id.seekBar))
    }

    @Test
    fun checkIsSeekbarMoves() {
        val message = "Is seekbar progress changes while music playing?"
        onView(withId(R.id.play_pause_button)).perform(click())
        Thread.sleep(2000)
        assertEquals(message, true, activity.seekBar.progress > 0)
    }
}