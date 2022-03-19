package org.hyperskill.musicplayer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
  lateinit var player: MediaPlayer
  lateinit var timer: Timer
  lateinit var seekBar: SeekBar
  lateinit var currentTimeView: TextView
  lateinit var totalTimeView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    player = MediaPlayer.create(this, R.raw.wisdom)
    seekBar = findViewById(R.id.seekBar)
    totalTimeView = findViewById(R.id.total_time_view)
    currentTimeView = findViewById(R.id.current_time_view)
    timer = Timer(seekBar, player, currentTimeView)
    totalTimeView.text = timer.calcTime(player.duration)
    val playPauseButton: ImageButton = findViewById(R.id.play_pause_button)
    playPauseButton.setOnClickListener {
      if (player.isPlaying) {
        player.pause()
        timer.stop()
      } else {
        player.start()
        timer.start()
      }
    }
    val stopButton: ImageButton = findViewById(R.id.stop_button)
    stopButton.setOnClickListener {
      player.stop()
      timer.reset()
    }
    seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
      override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if(fromUser) {
          currentTimeView.text = timer.calcTime(progress * 1000)
        }
      }
      override fun onStartTrackingTouch(seekBar: SeekBar?) {
        timer.stop()
      }
      override fun onStopTrackingTouch(seekBar: SeekBar?) {
        player.seekTo(seekBar!!.progress * 1000)
        timer.start()
      }
    })
    seekBar.max = player.duration / 1000
    timer.runTracker()
  }
}
