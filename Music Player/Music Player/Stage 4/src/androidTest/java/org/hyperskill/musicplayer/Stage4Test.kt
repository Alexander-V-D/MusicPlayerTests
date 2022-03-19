package org.hyperskill.musicplayer

import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import org.junit.Assert.*
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
        val titleList = listOf<String>("music1", "music2", "music3")
        onView(withId(R.id.search_button)).perform(click())
        for(i in 0..2) {
            onView(withContentDescription(titleList[i])).perform(click())
            Thread.sleep(4000)
            assertEquals(
                "Current time should be in 3..5, but it was " +
                        "${find<TextView>(R.id.current_time_view).text.toString()[4]}",
                true,
                find<TextView>(R.id.current_time_view).text.toString()[4].toChar() in '3'..'5')
            Thread.sleep(1000)
            assertEquals(
                "Current time should be in 4..6, but it was " +
                        "${find<TextView>(R.id.current_time_view).text.toString()[4]}",
                true,
                find<TextView>(R.id.current_time_view).text.toString()[4].toChar() in '4'..'6')
            Thread.sleep(1000)
            assertEquals(
                "Current time should be in 5..7, but it was " +
                        "${find<TextView>(R.id.current_time_view).text.toString()[4]}",
                true,
                find<TextView>(R.id.current_time_view).text.toString()[4].toChar() in '5'..'7')
        }
    }

    @Test
    fun checkIsMusicPlayingWhenCollapsed() {
        val message = "Is music playing while app collapsed?"
        onView(withId(R.id.search_button)).perform(click())
        onView(withContentDescription("music1")).perform(click())
        device = UiDevice.getInstance(getInstrumentation())
        device.pressHome()
        assertTrue(message, activity.player.isPlaying)
    }

    @Test
    fun checkIsMusicPlayingInSleepMode() {
        val message = "Is music playing while device in sleep mode?"
        onView(withId(R.id.search_button)).perform(click())
        onView(withContentDescription("music1")).perform(click())
        device = UiDevice.getInstance(getInstrumentation())
        device.sleep()
        assertTrue(message, activity.player.isPlaying)
    }
}