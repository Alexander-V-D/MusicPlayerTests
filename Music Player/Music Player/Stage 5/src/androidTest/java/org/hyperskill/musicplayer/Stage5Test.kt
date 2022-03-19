package org.hyperskill.musicplayer

import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Stage5Test: AbstractUnitTest<MainActivity>(MainActivity::class.java) {

    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun before() {
        activity = activityRule.activity
    }

    @Test
    fun checkIsPlaylistsButtonExists() {
        onView(withId(R.id.search_button)).perform(click())
        val message = "Is playlists_button exists?"
        assertNotNull(message, R.id.playlists_button)
    }

    @Test
    fun checkIsCheckBoxesExist() {
        onView(withId(R.id.search_button)).perform(click())
        val message = "Is checkboxes are on the song fragments?"
        for (i in 0..2) {
            assertNotNull(message, find<LinearLayout>(R.id.song_list).getChildAt(i)
                .findViewById(R.id.checkbox))
        }
    }

    @Test
    fun checkIsBackButtonVisibleBeforePlaylistCreating() {
        onView(withId(R.id.search_button)).perform(click())
        val message = "Is back button hidden from user before playlist creating"
        assertFalse(message, find<Button>(R.id.back_button).isVisible)
    }

    @Test
    fun checkIsPlaylistNameViewVisibleBeforePlaylistCreating() {
        onView(withId(R.id.search_button)).perform(click())
        val message = "Is playlist_name hidden from user before playlist creating"
        assertFalse(message, find<EditText>(R.id.playlist_name).isVisible)
    }

    @Test
    fun checkIsBackButtonVisibleWhilePlaylistCreating() {
        onView(withId(R.id.search_button)).perform(click())
        val checkBoxesContentDescriptionList = listOf(
            "Add song music1 to playlist",
            "Add song music2 to playlist",
            "Add song music3 to playlist")
        val message = "Is back button hidden while playlist creating"
        for (i in checkBoxesContentDescriptionList) {
            onView(withContentDescription(i)).perform(click())
            assertFalse(message, find<Button>(R.id.back_button).isVisible)
        }
    }

    @Test
    fun checkIsPlaylistNameViewVisibleWhilePlaylistCreating() {
        onView(withId(R.id.search_button)).perform(click())
        val musicList = listOf("music1", "music2", "music3")
        val message = "Is playlist_name view visible while playlist creating"
        onView(withContentDescription("Add song music1 to playlist")).perform(click())
//        onView(withHint("Enter playlist name")).perform(click())
        assertTrue(message, find<EditText>(R.id.playlist_name).isVisible)
    }

    @Test
    fun checkIsCreateButtonCreatesPlaylist() {
        onView(withId(R.id.search_button)).perform(click())
        val message = "Is create button creates playlist?"
        val musicList = listOf("music1", "music2")
        onView(withContentDescription("Add song music1 to playlist")).perform(click())
        onView(withContentDescription("Add song music2 to playlist")).perform(click())
        activity.playlistName.setText("qwerty")
        onView(withId(R.id.create_button)).perform(click())
        onView(withId(R.id.playlists_button)).perform(click())
        onView(withText("qwerty")).perform(click())
        onView(withText("OK")).perform(click())
        assertTrue(message, find<LinearLayout>(R.id.song_list).getChildAt(0)
            .findViewById<TextView>(R.id.song_title).text in musicList)
        assertTrue(message, find<LinearLayout>(R.id.song_list).getChildAt(1)
            .findViewById<TextView>(R.id.song_title).text in musicList)
        assertTrue(message, find<LinearLayout>(R.id.song_list).size == 2)
    }

    @Test
    fun checkIsBackButtonReturnsSongFragmentsOnScreen() {
        onView(withId(R.id.search_button)).perform(click())
        val message = "Is back button returns all songs?"
        val musicList = listOf("music1", "music2", "music3")
        onView(withContentDescription("Add song music1 to playlist")).perform(click())
        onView(withContentDescription("Add song music2 to playlist")).perform(click())
        activity.playlistName.setText("qwerty")
        onView(withId(R.id.create_button)).perform(click())
        onView(withId(R.id.playlists_button)).perform(click())
        onView(withText("qwerty")).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.back_button)).perform(click())
        for (i in 0..2) {
            assertTrue(message, find<LinearLayout>(R.id.song_list).getChildAt(i)
                .findViewById<TextView>(R.id.song_title).text in musicList)
        }
        assertTrue(message, find<LinearLayout>(R.id.song_list).size == 3)
    }
}