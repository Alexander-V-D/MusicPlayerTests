package com.example.boss

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible

class SongFragment(title: String, artist: String, id: Long, position: Int,
                   activity: MainActivity
) : Fragment() {
    val mTitle = title
    val mArtist = artist
    val mId = id
    var positionInList = position
    val mActivity = activity
    var titleView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
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
            val checkBox = view.findViewById<CheckBox>(R.id.checkbox)
            mActivity.checkBoxList.add(checkBox)
            if (mActivity.backButton.isVisible) checkBox.isVisible = false
            checkBox.contentDescription = "Add song $mTitle to playlist"
            checkBox.setOnClickListener {
                mActivity.switchButtonsBar(MainActivity.ButtonBar().creatingPlaylist)
                if (checkBox.isChecked) {
                    mActivity.songIdsForPlaylist.add(mId)
                    println("Id $mId added")
                    mActivity.checkedBoxesNumber += 1
                    println("Elements selected: ${mActivity.checkedBoxesNumber}")
                } else {
                    mActivity.songIdsForPlaylist.remove(mId)
                    println("Id $mId removed")
                    mActivity.checkedBoxesNumber -= 1
                    println("Elements selected: ${mActivity.checkedBoxesNumber}")
                    if (mActivity.checkedBoxesNumber == 0) {
                        mActivity.switchButtonsBar(MainActivity.ButtonBar().main)
                    }
                }
            }
        }
    }
}