package com.example.playlistmaker.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.data.model.TrackDto
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.util.TimeUtils
import com.example.playlistmaker.util.toPx

class AudioPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var viewModel: AudioPlayerViewModel

    companion object {
        const val DATA_TRACK = "trackData"
        const val TRACK_DURATION = "00:00"
        const val RADIUS = 8
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, AudioPlayerViewModel.getViewModelFactory(this))
            .get(AudioPlayerViewModel::class.java)

        val toolbar = binding.audioPlayerToolbar as Toolbar
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val track: TrackDto? = intent.getParcelableExtra(DATA_TRACK)

        track?.let {
            val trackDomainModel = TrackCreator.map(it)
            setupTrackDetails(it)
            viewModel.preparePlayer(it.previewUrl, trackDomainModel)
        }

        binding.playPauseButton.setOnClickListener {
            viewModel.playbackControl()
        }

        viewModel.isPlaying.observe(this) { isPlaying ->
            if (isPlaying) {
                binding.playPauseButton.setImageResource(R.drawable.pause)
            } else {
                binding.playPauseButton.setImageResource(R.drawable.play)
            }
        }

        viewModel.trackDuration.observe(this) { duration ->
            binding.playerTrackDuration.text = duration
        }

        viewModel.playerState.observe(this) { state ->
            if (state == AudioPlayerViewModel.STATE_PREPARED) {
                viewModel.startUpdatingTime()
            } else if (state == AudioPlayerViewModel.STATE_PLAYING) {
                viewModel.startUpdatingTime()
            }

        }
        binding.trackDurationLabel.text = getString(R.string.duration)
        binding.albumNameLabel.text = getString(R.string.album)
        binding.trackYearLabel.text = getString(R.string.releaseYear)
        binding.trackGenreLabel.text = getString(R.string.genre)
        binding.trackCountryLabel.text = getString(R.string.country)


        binding.addToFavoriteButton.setOnClickListener {
            viewModel.toggleFavorite()
        }
        viewModel.isFavorite.observe(this) { isFavorite ->
            binding.addToFavoriteButton.setImageResource(
                if (isFavorite) R.drawable.favourites_ok else R.drawable.favourites
            )
            binding.addToFavoriteButton.visibility = View.VISIBLE
        }

        binding.addToPlaylistButton.setOnClickListener {
            viewModel.togglePlaylist()
        }


        viewModel.isInPlaylist.observe(this) { isInPlaylist ->
            binding.addToPlaylistButton.setImageResource(
                if (isInPlaylist) R.drawable.plus_ok else R.drawable.plus
            )
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseAudio()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }

    private fun setupTrackDetails(track: TrackDto) {
        binding.trackName.text = track.trackName
        binding.trackArtistName.text = track.artistName
        binding.albumNameValue.text = track.collectionName ?: getString(R.string.unknownAlbum)
        binding.trackYearValue.text =
            track.releaseDate?.substring(0, 4) ?: getString(R.string.unknownReleaseDate)
        binding.trackGenreValue.text = track.primaryGenreName ?: getString(R.string.unknownGenre)
        binding.trackCountryValue.text = track.country ?: getString(R.string.unknownCountry)
        binding.trackDurationValue.text = TimeUtils.formatTime(track.trackTimeMillis)
        binding.playerTrackDuration.text = TRACK_DURATION

        Glide.with(this)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.placeholder_album_player)
            .transform(RoundedCorners(this.toPx(RADIUS).toInt()))
            .into(binding.albumArt)
    }
}