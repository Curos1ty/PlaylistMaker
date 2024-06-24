package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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

    companion object {
        const val DATA_TRACK = "trackData"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        albumArtImageView = findViewById(R.id.album_art)
        trackNameTextView = findViewById(R.id.track_name)
        artistNameTextView = findViewById(R.id.track_artist_name)
        collectionNameTextView = findViewById(R.id.album_name_value)
        releaseDateTextView = findViewById(R.id.track_year_value)
        primaryGenreNameTextView = findViewById(R.id.track_genre_value)
        countryTextView = findViewById(R.id.track_country_value)
        trackDurationTextView = findViewById(R.id.track_duration_value)

        val addToPlaylistButton = findViewById<ImageButton>(R.id.add_to_playlist_button)
        val addToFavoriteButton = findViewById<ImageButton>(R.id.add_to_favourite_button)
        val playPauseButton = findViewById<ImageButton>(R.id.play_pause_button)

        var checkPlay = true
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

        val track = intent.getParcelableExtra<Track>(DATA_TRACK)

        track?.let {
            trackNameTextView.text = it.trackName
            artistNameTextView.text = it.artistName
            if (it.collectionName != null) {
                collectionNameTextView.text = it.collectionName
            }
            releaseDateTextView.text = it.releaseDate.substring(0, 4)
            primaryGenreNameTextView.text = it.primaryGenreName
            countryTextView.text = it.country
            trackDurationTextView.text = formatTrackDuration(it.trackTimeMillis)
            playerTrackDurationTextView.text = formatTrackDuration(it.trackTimeMillis)

            Glide.with(this)
                .load(it.artworkUrl512)
                .placeholder(R.drawable.placeholder_album_player)
                .transform(RoundedCorners(this.toPx(8).toInt()))
                .into(albumArtImageView)
        }
        durationTextLabel.text = getString(R.string.duration)
        albumTextLabel.text = getString(R.string.album)
        releaseDateTextLabel.text = getString(R.string.releaseYear)
        genreTextLabel.text = getString(R.string.genre)
        countryTextLabel.text = getString(R.string.country)



        playPauseButton.setOnClickListener {
            checkPlay = if (checkPlay) {
                playPauseButton.setImageResource(R.drawable.pause)
                false
            } else {
                playPauseButton.setImageResource(R.drawable.play)
                true
            }
        }

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

    private fun formatTrackDuration(trackTimeMillis: Long): String {
        val minutes = (trackTimeMillis / 1000) / 60
        val seconds = (trackTimeMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}