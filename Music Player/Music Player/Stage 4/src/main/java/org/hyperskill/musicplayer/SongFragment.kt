package org.hyperskill.musicplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

class SongFragment(title: String, artist: String, id: Long, position: Int,
                   activity: MainActivity
) : Fragment() {
    val mTitle = title
    val mArtist = artist
    val mId = id
    var positionInList = position
    val mActivity = activity
    var titleView: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_song, container, false)
    }

    override fun onStart() {
        super.onStart()
        val view = view
        if (view != null) {
            println("Song fragment $positionInList exist")
            titleView = view.findViewById(R.id.song_title)
            titleView?.text = mTitle
            val artistView = view.findViewById<TextView>(R.id.song_artist)
            artistView.text = mArtist
            val playButton = view.findViewById<ImageButton>(R.id.song_status_button)
            playButton?.setOnClickListener {
                mActivity.onPlayPauseSongButtonClick(positionInList, mActivity,
                    view.findViewById(R.id.song_status_button))
            }
            playButton.contentDescription = mTitle
        }
    }
}