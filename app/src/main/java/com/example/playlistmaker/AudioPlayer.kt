package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.model.Track

class AudioPlayer : AppCompatActivity() {
    private lateinit var albumArtImageView: ImageView
    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var collectionNameTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var primaryGenreNameTextView: TextView
    private lateinit var countryTextView: TextView
    private lateinit var trackDurationTextView: TextView

    private lateinit var playerTrackDurationTextView: TextView
    private lateinit var durationTextLabel: TextView
    private lateinit var albumTextLabel: TextView
    private lateinit var releaseDateTextLabel: TextView
    private lateinit var genreTextLabel: TextView
    private lateinit var countryTextLabel: TextView

    private lateinit var playPauseButton: ImageButton
    private lateinit var addToPlaylistButton: ImageButton
    private lateinit var addToFavoriteButton: ImageButton

    companion object {
        const val DATA_TRACK = "trackData"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        const val TRACK_DURATION = "00:00"
        const val RADIUS = 8
    }

    private var isPlaying = false
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private var updateTimeRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        playPauseButton = findViewById(R.id.play_pause_button)

        playPauseButton.setOnClickListener { playbackControl() }

        albumArtImageView = findViewById(R.id.album_art)
        trackNameTextView = findViewById(R.id.track_name)
        artistNameTextView = findViewById(R.id.track_artist_name)
        collectionNameTextView = findViewById(R.id.album_name_value)
        releaseDateTextView = findViewById(R.id.track_year_value)
        primaryGenreNameTextView = findViewById(R.id.track_genre_value)
        countryTextView = findViewById(R.id.track_country_value)
        trackDurationTextView = findViewById(R.id.track_duration_value)

        addToPlaylistButton = findViewById(R.id.add_to_playlist_button)
        addToFavoriteButton = findViewById(R.id.add_to_favourite_button)


        var checkAddPlayList = true
        var checkFav = true
        playPauseButton.setImageResource(R.drawable.play)
        addToFavoriteButton.setImageResource(R.drawable.favourites)
        addToPlaylistButton.setImageResource(R.drawable.plus)

        playerTrackDurationTextView = findViewById(R.id.playerTrackDuration)
        durationTextLabel = findViewById(R.id.track_duration_label)
        albumTextLabel = findViewById(R.id.album_name_label)
        releaseDateTextLabel = findViewById(R.id.track_year_label)
        genreTextLabel = findViewById(R.id.track_genre_label)
        countryTextLabel = findViewById(R.id.track_country_label)


        val toolbar = findViewById<Toolbar>(R.id.audio_player_toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val track: Track? = intent.getParcelableExtra(DATA_TRACK)

        track?.let {
            setupTrackDetails(it)
            preparePlayer(it.previewUrl)
        }
        durationTextLabel.text = getString(R.string.duration)
        albumTextLabel.text = getString(R.string.album)
        releaseDateTextLabel.text = getString(R.string.releaseYear)
        genreTextLabel.text = getString(R.string.genre)
        countryTextLabel.text = getString(R.string.country)


        addToFavoriteButton.setOnClickListener {
            checkFav = if (checkFav) {
                addToFavoriteButton.setImageResource(R.drawable.favourites_ok)
                false
            } else {
                addToFavoriteButton.setImageResource(R.drawable.favourites)
                true
            }

        }

        addToPlaylistButton.setOnClickListener {
            checkAddPlayList = if (checkAddPlayList) {
                addToPlaylistButton.setImageResource(R.drawable.plus_ok)
                false
            } else {
                addToPlaylistButton.setImageResource(R.drawable.plus)
                true
            }

        }

    }

    private fun setupTrackDetails(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName
        if (track.collectionName != null) {
            collectionNameTextView.text = track.collectionName
        }
        releaseDateTextView.text =
            track.releaseDate?.substring(0, 4) ?: getString(R.string.unknownReleaseDate)
        primaryGenreNameTextView.text = track.primaryGenreName ?: getString(R.string.unknownGenre)
        countryTextView.text = track.country ?: getString(R.string.unknownCountry)
        trackDurationTextView.text = formatTrackDuration(track.trackTimeMillis)
        playerTrackDurationTextView.text = TRACK_DURATION

        Glide.with(this)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.placeholder_album_player)
            .transform(RoundedCorners(this.toPx(RADIUS).toInt()))
            .into(albumArtImageView)


    }

    private fun formatTime(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        pauseAudio()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            playPauseButton.setImageResource(R.drawable.play)
            isPlaying = false
            updateTimeRunnable?.let { handler.removeCallbacks(it) }
            playerTrackDurationTextView.text = formatTime(0)

        }

        updateTimeRunnable = object : Runnable {
            override fun run() {
                mediaPlayer.let {
                    if (it.isPlaying) {
                        playerTrackDurationTextView.text = formatTime(it.currentPosition)
                        handler.postDelayed(this, 500)
                    }
                }
            }
        }
    }

    private fun playAudio() {
        mediaPlayer.start()
        isPlaying = true
        playPauseButton.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        updateTimeRunnable?.let { handler.post(it) }
    }

    private fun pauseAudio() {
        mediaPlayer.pause()
        isPlaying = false
        playPauseButton.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
        updateTimeRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pauseAudio()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                playAudio()
            }
        }
    }

    private fun formatTrackDuration(trackTimeMillis: Long): String {
        val minutes = (trackTimeMillis / 1000) / 60
        val seconds = (trackTimeMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}