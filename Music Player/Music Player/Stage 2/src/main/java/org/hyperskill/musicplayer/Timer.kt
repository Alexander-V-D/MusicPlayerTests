package org.hyperskill.musicplayer

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import android.widget.TextView
import java.lang.Runnable
import java.util.*

class Timer(seekBar: SeekBar, player: MediaPlayer, timeView: TextView) {
    var running = false
    val handler = Handler(Looper.getMainLooper())
    val mSeekBar = seekBar
    private val mPlayer = player
    val mTimeView = timeView
    private val runnable: Runnable = object: Runnable {
        override fun run() {
            if (running) {
                mSeekBar.progress = getTime() / 1000
                mTimeView.text = calcTime(getTime())
                println(calcTime(getTime()))
            }
            handler.postDelayed(this, 1000)
        }
    }
    fun stop() {
        running = false
    }
    fun start() {
        running = true
    }
    fun reset() {
        running = false
        mSeekBar.progress = 0
        mTimeView.text = calcTime(0)
    }
    fun calcTime(time: Int): String {
        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            time / 60_000, time % 60_000 / 1000
        )
    }
    private fun getTime() = mPlayer.currentPosition
    fun runTracker() {
        handler.post(runnable)
    }
}