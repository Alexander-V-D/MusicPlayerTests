package org.hyperskill.musicplayer

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
class Stage1Test: org.hyperskill.musicplayer.AbstractUnitTest<MainActivity>(MainActivity::class.java) {

    @Rule @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun before() {
        activity = activityRule.activity
    }

    @Test
    fun checkIsSongTitleExist() {
        val message = "Is song title visible for user?"
        assertNotNull(message, find(R.id.song_title))
    }

    @Test
    fun checkIsSongArtistExist() {
        val message = "Is song artist name visible for user?"
        assertNotNull(message, find(R.id.song_artist))
    }

    @Test
    fun checkIsPlayPauseButtonExist() {
        val message = "Is play_pause_button visible for user?"
        assertNotNull(message, find(R.id.play_pause_button))
    }

    @Test
    fun checkIsStopButtonExist() {
        val message = "Is stop_button visible for user?"
        assertNotNull(message, find(R.id.stop_button))
    }

    @Test
    fun checkIsPlayPauseButtonWorks() {
        onView(withId(R.id.play_pause_button)).perform(click())
        val message = "Does play_pause_button controls playback?"
        assertEquals(message, true, activityRule.activity.player.isPlaying)
        onView(withId(R.id.play_pause_button)).perform(click())
        assertEquals(message, false, activityRule.activity.player.isPlaying)
    }

    @Test
    fun checkIsStopButtonWorks() {
        onView(withId(R.id.play_pause_button)).perform(click())
        val message = "Does stop button stops playback?"
        onView(withId(R.id.stop_button)).perform(click())
        assertEquals(message, false, activity.player.isPlaying)
    }
}