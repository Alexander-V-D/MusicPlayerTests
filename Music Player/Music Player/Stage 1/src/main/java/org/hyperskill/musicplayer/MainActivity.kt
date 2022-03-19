package org.hyperskill.musicplayer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
  lateinit var player: MediaPlayer

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    player = MediaPlayer.create(this, R.raw.wisdom)
    val playPauseButton = findViewById<ImageButton>(R.id.play_pause_button)
    playPauseButton.setOnClickListener {
      if (player.isPlaying) {
        player.pause()
      } else {
        player.start()
      }
    }
    val stopButton = findViewById<ImageButton>(R.id.stop_button)
    stopButton.setOnClickListener {
      player.stop()
    }
  }
}
