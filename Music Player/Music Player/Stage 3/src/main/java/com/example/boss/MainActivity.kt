package com.example.boss

import android.Manifest
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
  lateinit var player: MediaPlayer
  lateinit var timer: Timer
  lateinit var seekBar: SeekBar
  lateinit var currentTimeView: TextView
  lateinit var totalTimeView: TextView
  lateinit var searchButton: Button
  lateinit var playPauseButton: Button
//  var playerIsEmpty = true
//  lateinit var currentSongStatusButton: ImageButton
//  var currentPlayingSongPosition = 0
  data class Song(val title: String, val artist: String, val id: Long)
  val songFragmentList = mutableListOf<SongFragment>()
  val songList = mutableListOf<Song>()
//  val songs = mutableListOf<Song>()
//  var statusButtonsList = mutableListOf<ImageButton>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    player = MediaPlayer.create(this, R.raw.wisdom)
    seekBar = findViewById(R.id.seekbar)
    totalTimeView = findViewById(R.id.total_time_view)
    currentTimeView = findViewById(R.id.current_time_view)
    timer = Timer(seekBar, player, currentTimeView)
    totalTimeView.text = timer.calcTime(player.duration)
    searchButton = findViewById(R.id.search_button)
    playPauseButton = findViewById(R.id.play_pause_button)
    askWritePermission()
    playPauseButton.setOnClickListener {
      if (player.isPlaying) {
        player.pause()
        timer.stop()
      } else {
        player.start()
        timer.start()
        println("playing: ${player.isPlaying}")
      }
    }
    val stopButton = findViewById<Button>(R.id.stop_button)
    stopButton.setOnClickListener {
      player.stop()
      timer.reset()
    }
    seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
      override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if(fromUser) {
          currentTimeView.text = timer.calcTime(progress * 1000)
          println("Progress changed to: ${timer.calcTime(progress * 1000)} by user")
        }
      }
      override fun onStartTrackingTouch(seekBar: SeekBar?) {
        timer.stop()
        println("Progress is tracking")
      }
      override fun onStopTrackingTouch(seekBar: SeekBar?) {
        player.seekTo(seekBar!!.progress * 1000)
        timer.start()
        println("Progress is not tracking")
      }
    })
    searchButton.setOnClickListener {
      updateSongListFromStorage()
      placeSongFragmentsOnScreen(songList)
    }
    seekBar.max = player.duration / 1000
    timer.runTracker()

  }
  private fun updateSongListFromStorage() {
    val musicResolver = contentResolver
    val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val musicCursor = musicResolver.query(musicUri, null, null,
      null, null, null)
//    val songList = mutableListOf<Song>()
    if (musicCursor != null && musicCursor.moveToFirst()) {
      val titleColumn = musicCursor.getColumnIndex(
        android.provider.MediaStore.Audio.Media.TITLE)
      val artistColumn = musicCursor.getColumnIndex(
        android.provider.MediaStore.Audio.Media.ARTIST)
      val idColumn = musicCursor.getColumnIndex(
        android.provider.MediaStore.Audio.Media._ID)
      do {
        val title = musicCursor.getString(titleColumn)
        val artist = musicCursor.getString(artistColumn)
        val id = musicCursor.getLong(idColumn)
        songList.add(Song(title, artist, id))
      } while (musicCursor.moveToNext())
    }
    musicCursor?.close()
  }
  private fun placeSongFragmentsOnScreen(songList: MutableList<Song>) {
    val tr = supportFragmentManager.beginTransaction()
    for (i in songList) {
      tr.add(
        R.id.song_list, SongFragment(
          i.title, i.artist, i.id, songList.indexOf(i), this
        )
      )
      println("fragment ${i.id} added")
      songFragmentList.add(
        SongFragment(i.title, i.artist, i.id, songList.indexOf(i), this)
      )
    }
    tr.commit()
  }
  private fun askWritePermission() {
    ActivityCompat.requestPermissions(this,
      arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE),
      101)
  }
//  private fun playNewSong(context: MainActivity) {
//    if (!playerIsEmpty) {
//      player.stop()
//      player.reset()
//      timer.stop()
//    }
//    val currentSongResourceId = songs[currentPlayingSongPosition!!].id
//    val trackUri = ContentUris.withAppendedId(
//      android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSongResourceId)
//    player = MediaPlayer.create(context, trackUri)
////    player.setOnCompletionListener(this)
//    player.start()
//    println("playing new: ${player.isPlaying}")
//    playerIsEmpty = false
//    context.seekBar.max = player.duration / 1000
//    timer = Timer(context.seekBar, player, context.currentTimeView)
//    context.totalTimeView.text = timer.calcTime(player.duration)
//    timer.runTracker()
//    timer.start()
//  }
//  fun onPlayPauseSongButtonClick(songPositionInList: Int, activity: MainActivity,
//                                 status: ImageButton
//  ) {
//    if (playerIsEmpty) {
//      currentSongStatusButton = status
//      playPauseButton = activity.playPauseButton
//      statusButtonsList = activity.statusButtonsList
////      activity.enableController()
//    }
//    if (currentPlayingSongPosition != songPositionInList || playerIsEmpty) {
//      currentSongStatusButton!!.setImageResource(R.drawable.ic_action_play)
//      currentPlayingSongPosition = songPositionInList
//      status.setImageResource(R.drawable.ic_action_pause)
//      currentSongStatusButton = status
//      playPauseButton.setImageResource(R.drawable.ic_action_pause)
//      playNewSong(activity)
//    } else if (player.isPlaying) {
//      status.setImageResource(R.drawable.ic_action_play)
//      playPauseButton.setImageResource(R.drawable.ic_action_play)
//      pause(activity)
//    } else {
//      status.setImageResource(R.drawable.ic_action_pause)
//      playPauseButton.setImageResource(R.drawable.ic_action_pause)
//      resume(activity)
//    }
//  }
//  fun onControllerPlayPauseButtonClick(activity: MainActivity) {
//    if (!playerIsEmpty) {
//      if (player.isPlaying) {
//        currentSongStatusButton!!.setImageResource(R.drawable.ic_action_play)
//        playPauseButton.setImageResource(R.drawable.ic_action_play)
//        pause(activity)
//      } else {
//        playPauseButton.setImageResource(R.drawable.ic_action_pause)
//        currentSongStatusButton!!.setImageResource(R.drawable.ic_action_pause)
//        resume(activity)
//      }
//    }
//  }
//  private fun pause(activity: MainActivity) {
//    player.pause()
//    println("paused: ${player.isPlaying}")
//    timer.stop()
//  }
//  private fun resume(activity: MainActivity) {
//    player.start()
//    println("resumed: ${player.isPlaying}")
//    timer.start()
//  }
}
