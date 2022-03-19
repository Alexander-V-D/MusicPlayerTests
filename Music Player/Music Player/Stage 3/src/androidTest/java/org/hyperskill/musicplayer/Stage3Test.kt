package org.hyperskill.musicplayer

import android.content.pm.PackageManager
import android.widget.LinearLayout
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
class Stage3Test: AbstractUnitTest<MainActivity>(MainActivity::class.java) {

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
        val message = "Is search_button exist?"
        assertNotNull(message, find(R.id.search_button))
    }

    @Test
    fun checkHaveReadPermission() {
        val message = "Is read permission granted?"
        assertEquals(message, PackageManager.PERMISSION_GRANTED,
            activity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE"))
    }

    @Test
    fun checkHowMuchFilesProgramSee() {
        val message = "Is program see all three files"
        onView(withId(R.id.search_button)).perform(click())
        assertEquals(message, 3, activity.songList.size)
    }

    @Test
    fun checkDoesProgramSeeMusic() {
        val message = "Does search_button place all fragments on the screen?"
        val message2 = "Does song fragments displaying song title and artist?"
        onView(withId(R.id.search_button)).perform(click())
        for (i in 0..activity.songList.lastIndex) {
            assertNotNull(message2, find<LinearLayout>(R.id.song_list).getChildAt(i))
        }
        for (i in 0..activity.songList.lastIndex) {
            assertNotNull(message, find<LinearLayout>(R.id.song_list).getChildAt(i).findViewById(R.id.song_title))
            assertNotNull(message, find<LinearLayout>(R.id.song_list).getChildAt(i).findViewById(R.id.song_artist))
        }
    }
}
