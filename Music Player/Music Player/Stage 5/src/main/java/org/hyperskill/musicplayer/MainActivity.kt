package org.hyperskill.musicplayer

import android.Manifest
import android.app.AlertDialog
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {
  lateinit var player: MediaPlayer
  lateinit var timer: Timer
  lateinit var seekBar: SeekBar
  lateinit var currentTimeView: TextView
  lateinit var totalTimeView: TextView
  lateinit var searchButton: Button
  lateinit var playPauseButton: ImageButton
  lateinit var songListView: LinearLayout
  data class Song(val title: String, val artist: String, val id: Long)
  val songFragmentList = mutableListOf<SongFragment>()
  val songList = mutableListOf<Song>()
  var playerIsEmpty = true
  var currentSongStatusButton: ImageButton? = null
  var currentPlayingSongPosition: Int? = null
  var songs = mutableListOf<Song>()
  var statusButtonsList = mutableListOf<ImageButton>()
  var checkBoxList = mutableListOf<CheckBox>()
  var songIdsForPlaylist = mutableListOf<Long>()
  var checkedBoxesNumber = 0
  lateinit var playlistDataBaseHelper: PlaylistsDataBaseHelper
  lateinit var dataBase: SQLiteDatabase
  lateinit var createPlaylistButton: Button
  lateinit var playlistsButton: Button
  lateinit var playlistName: EditText
  lateinit var backButton: Button

  class ButtonBar {
    val onCreate = "onCreate"
    val main = "main"
    val creatingPlaylist = "creatingPlaylist"
    val playlist = "playlist"
  }
  class PlaylistsDataBaseHelper(context: Context, var dbName: String = "Playlists", var dbVersion: Int = 1):
    SQLiteOpenHelper(context, dbName, null, dbVersion) {

    override fun onCreate(db: SQLiteDatabase?) {
      updateDatabase(db, 0, dbVersion)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
      updateDatabase(db, oldVersion, newVersion)
    }
    private fun updateDatabase(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
      if (oldVersion < 1) {
        db!!.execSQL("CREATE TABLE SONGS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "PLAYLIST TEXT, "
                + "SONG_ID INTEGER);")
        println("db created")
      }
    }
    fun createNewPlaylist(db: SQLiteDatabase, songIdList: MutableList<Long>, playlistName: String) {
      for (i in songIdList) {
        val values = ContentValues()
        values.put("PLAYLIST", playlistName)
        values.put("SONG_ID", i.toInt())
        db.insert("SONGS", null, values)
      }
    }
    fun deletePlaylist(db: SQLiteDatabase, playlist: String) {
      db.delete("SONGS",
        "PLAYLIST = ?", arrayOf(playlist))
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    seekBar = findViewById(R.id.seekbar)
    totalTimeView = findViewById(R.id.total_time_view)
    currentTimeView = findViewById(R.id.current_time_view)
    playPauseButton = findViewById(R.id.play_pause_button)
    searchButton = findViewById(R.id.search_button)
    songListView = findViewById(R.id.song_list)
    playlistDataBaseHelper = PlaylistsDataBaseHelper(this)
    searchButton = findViewById(R.id.search_button)
    createPlaylistButton = findViewById(R.id.create_button)
    playlistsButton = findViewById(R.id.playlists_button)
    playlistName = findViewById(R.id.playlist_name)
    backButton = findViewById(R.id.back_button)
    dataBase = playlistDataBaseHelper.writableDatabase
    switchButtonsBar(ButtonBar().onCreate)
    askWritePermission()
    val playPauseButton = findViewById<ImageButton>(R.id.play_pause_button)
    playPauseButton.setOnClickListener {
      onControllerPlayPauseButtonClick(this)
    }
    val stopButton = findViewById<ImageButton>(R.id.stop_button)
    stopButton.setOnClickListener {
      stop()
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
    searchButton.setOnClickListener {
      placeSongFragmentsOnScreen(getSongListFromStorage())
      switchButtonsBar(ButtonBar().main)
    }
    backButton.setOnClickListener {
      songListView.removeAllViewsInLayout()
      switchButtonsBar(ButtonBar().main)
      songFragmentList.clear()
      songList.clear()
      statusButtonsList.clear()
      val songList = getSongListFromStorage()
      placeSongFragmentsOnScreen(songList)
      songs = songList
      if (!playerIsEmpty) {
        stop()
      }
    }
    createPlaylistButton.setOnClickListener {
      if (playlistName.text.isNotEmpty()) {
        playlistDataBaseHelper.createNewPlaylist(
          dataBase, songIdsForPlaylist,
          playlistName.text.toString()
        )
        switchButtonsBar(ButtonBar().main)
        playlistName.text = null
        for (i in checkBoxList) {
          i.isChecked = false
        }
        checkedBoxesNumber = 0
      }
    }
    playlistsButton.setOnClickListener {
      buildDialogList()
    }
  }
  private fun getSongListFromStorage(): MutableList<Song> {
    val musicResolver = contentResolver
    val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val musicCursor = musicResolver.query(musicUri, null, null,
      null, null, null)
    val songList = mutableListOf<Song>()
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
    return songList
  }
  private fun placeSongFragmentsOnScreen(songList: MutableList<Song>) {
    val tr = supportFragmentManager.beginTransaction()
    for (i in songList) {
      tr.add(
        R.id.song_list, SongFragment(
          i.title, i.artist, i.id, songList.indexOf(i), this
        )
      )
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
  private fun playNewSong(context: MainActivity) {
    if (!playerIsEmpty) {
      player.stop()
      player.reset()
      timer.stop()
    }
    val currentSongResourceId = getSongListFromStorage()[currentPlayingSongPosition!!].id
    val trackUri = ContentUris.withAppendedId(
      android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSongResourceId)
    player = MediaPlayer.create(context, trackUri)
    player.start()
    playerIsEmpty = false
    context.seekBar.max = player.duration / 1000
    timer = Timer(context.seekBar, player, context.currentTimeView)
    context.totalTimeView.text = timer.calcTime(player.duration)
    timer.runTracker()
    timer.start()
  }
  fun onPlayPauseSongButtonClick(songPositionInList: Int, activity: MainActivity,
                                 status: ImageButton
  ) {
    if (playerIsEmpty) {
      currentSongStatusButton = status
      playPauseButton = activity.playPauseButton
      statusButtonsList = activity.statusButtonsList
    }
    if (currentPlayingSongPosition != songPositionInList || playerIsEmpty) {
      currentSongStatusButton!!.setImageResource(R.drawable.ic_action_play)
      currentPlayingSongPosition = songPositionInList
      status.setImageResource(R.drawable.ic_action_pause)
      currentSongStatusButton = status
      playPauseButton.setImageResource(R.drawable.ic_action_pause)
      playNewSong(activity)
    } else if (player.isPlaying) {
      status.setImageResource(R.drawable.ic_action_play)
      playPauseButton.setImageResource(R.drawable.ic_action_play)
      pause()
    } else {
      status.setImageResource(R.drawable.ic_action_pause)
      playPauseButton.setImageResource(R.drawable.ic_action_pause)
      resume()
    }
  }
  fun onControllerPlayPauseButtonClick(activity: MainActivity) {
    if (!playerIsEmpty) {
      if (player.isPlaying) {
        currentSongStatusButton!!.setImageResource(R.drawable.ic_action_play)
        playPauseButton.setImageResource(R.drawable.ic_action_play)
        pause()
      } else {
        playPauseButton.setImageResource(R.drawable.ic_action_pause)
        currentSongStatusButton!!.setImageResource(R.drawable.ic_action_pause)
        resume()
      }
    }
  }
  private fun pause() {
    player.pause()
    timer.stop()
  }
  private fun resume() {
    player.start()
    timer.start()
  }
  fun stop() {
    player.reset()
    playerIsEmpty = true
    currentSongStatusButton!!.setImageResource(R.drawable.ic_action_play)
    playPauseButton.setImageResource(R.drawable.ic_action_play)
    currentPlayingSongPosition = null
    currentSongStatusButton = null
    timer.reset()
  }
  private fun buildDialogList() {
    var selectedItem = -1
    val builder = AlertDialog.Builder(this)
    val playlists = getPlaylistsList().toTypedArray()
    builder.setTitle("Choose playlist")
      .setSingleChoiceItems(playlists, -1) {
          _, item -> selectedItem = item
      }
      .setPositiveButton("OK") {
          _, _ ->
        if (selectedItem != -1) {
          val songIds = getSongIdsFromPlaylist(playlists[selectedItem])
          songListView.removeAllViewsInLayout()
          statusButtonsList.clear()
          val tr = supportFragmentManager.beginTransaction()
          for (i in getSongListFromStorage()) {
            if (i.id in songIds) {
              tr.add(
                R.id.song_list, SongFragment(
                  i.title, i.artist, i.id, getSongListFromStorage().indexOf(i), this
                )
              )
            }
          }
          tr.commit()
          switchButtonsBar(ButtonBar().playlist)
          if (!playerIsEmpty) {
            stop()
          }
        }
      }
      .setNeutralButton("CANCEL") {
          _, _ ->
      }
      .setNegativeButton("DELETE") {
          _, _ ->
        if(selectedItem != -1) {
          playlistDataBaseHelper.deletePlaylist(dataBase, playlists[selectedItem])
        }
      }
    builder.show()
  }
  private fun getPlaylistsList(): MutableList<String> {
    val cursor = dataBase.query(
      "SONGS", arrayOf("PLAYLIST"), null,
      null, null, null, null
    )
    val playlistsList = mutableListOf<String>()
    if (cursor != null && cursor.moveToFirst()) {
      do {
        if (cursor.getString(0) in playlistsList) continue
        playlistsList.add(cursor.getString(0))
      } while (cursor.moveToNext())
    }
    cursor.close()
    return playlistsList
  }
  private fun getSongIdsFromPlaylist(playlist: String): MutableList<Long> {
    val cursor = dataBase.query(
      "SONGS", arrayOf("SONG_ID"), "PLAYLIST = ?",
      arrayOf(playlist), null, null, null
    )
    val songIdsList = mutableListOf<Long>()
    if (cursor != null && cursor.moveToFirst()) {
      do {
        songIdsList.add(cursor.getInt(0).toLong())
      } while (cursor.moveToNext())
    }
    cursor.close()
    return songIdsList
  }
  fun switchButtonsBar(command: String) {
    when(command) {
      "onCreate" -> {
        searchButton.isVisible = true
        playlistName.isVisible = false
        playlistName.isEnabled = false
        backButton.isVisible = false
        createPlaylistButton.isVisible = false
        playlistsButton.isVisible = false
      }
      "main" -> {
        searchButton.isVisible = true
        playlistName.isVisible = false
        playlistName.isEnabled = false
        backButton.isVisible = false
        createPlaylistButton.isVisible = false
        playlistsButton.isVisible = true
      }
      "creatingPlaylist" -> {
        searchButton.isVisible = false
        playlistName.isVisible = true
        playlistName.isEnabled = true
        backButton.isVisible = false
        createPlaylistButton.isVisible = true
        playlistsButton.isVisible = false
      }
      "playlist" -> {
        searchButton.isVisible = false
        playlistName.isVisible = false
        playlistName.isEnabled = false
        backButton.isVisible = true
        createPlaylistButton.isVisible = false
        playlistsButton.isVisible = false
      }
    }
  }
}
