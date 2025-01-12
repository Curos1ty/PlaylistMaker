package com.example.playlistmaker.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.data.model.TrackDto
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.presentation.adapter.PlaylistAdapter
import com.example.playlistmaker.presentation.ui.media.CreatePlaylistFragment
import com.example.playlistmaker.presentation.ui.media.PlaylistTrackStatus
import com.example.playlistmaker.util.TimeUtils
import com.example.playlistmaker.util.toPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private val audioPlayerViewModel: AudioPlayerViewModel by viewModel()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)

        setupBottomSheet()
        setupPlaylistBottomSheet()
        audioPlayerViewModel.loadPlaylists()

        val toolbar = binding.audioPlayerToolbar
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val track: TrackDto? = intent.getParcelableExtra(DATA_TRACK)

        track?.let {
            val trackDomainModel = TrackCreator.map(it)
            setupTrackDetails(it)
            audioPlayerViewModel.preparePlayer(it.previewUrl, trackDomainModel)
        }

        binding.playPauseButton.setOnClickListener {
            audioPlayerViewModel.playbackControl()
        }

        audioPlayerViewModel.isPlaying.observe(this) { isPlaying ->
            if (isPlaying) {
                binding.playPauseButton.setImageResource(R.drawable.pause)
            } else {
                binding.playPauseButton.setImageResource(R.drawable.play)
            }
        }

        audioPlayerViewModel.trackDuration.observe(this) { duration ->
            binding.playerTrackDuration.text = duration
        }

        audioPlayerViewModel.playerState.observe(this) { state ->
            if (state == AudioPlayerViewModel.STATE_PREPARED) {
                audioPlayerViewModel.startUpdatingTime()
            } else if (state == AudioPlayerViewModel.STATE_PLAYING) {
                audioPlayerViewModel.startUpdatingTime()
            }

        }

        binding.trackDurationLabel.text = getString(R.string.duration)
        binding.albumNameLabel.text = getString(R.string.album)
        binding.trackYearLabel.text = getString(R.string.releaseYear)
        binding.trackGenreLabel.text = getString(R.string.genre)
        binding.trackCountryLabel.text = getString(R.string.country)

        setupFavoriteButton()
        setupPlaylistButton()

        binding.createPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            binding.fragmentContainer.visibility = View.VISIBLE

            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    CreatePlaylistFragment()
                )
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onPause() {
        super.onPause()
        if (audioPlayerViewModel.isPlaying.value == true) {
            audioPlayerViewModel.pauseAudio()
        }
    }

    override fun onResume() {
        super.onResume()
        if (audioPlayerViewModel.isPlaying.value == true) {
            audioPlayerViewModel.playAudio()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerViewModel.releasePlayer()
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

    private fun setupFavoriteButton() {
        audioPlayerViewModel.isFavorite.observe(this) { isFavorite ->
            binding.addToFavoriteButton.setImageResource(
                if (isFavorite) R.drawable.favourites_ok else R.drawable.favourites
            )
        }
        binding.addToFavoriteButton.setOnClickListener {
            audioPlayerViewModel.toggleFavorite()
        }
    }

    private fun setupPlaylistButton() {
        audioPlayerViewModel.isInPlaylist.observe(this) { isInPlayList ->
            binding.addToPlaylistButton.setImageResource(
                if (isInPlayList) R.drawable.plus_ok else R.drawable.plus
            )
        }
        binding.addToPlaylistButton.setOnClickListener {
            updateBottomSheetState()
        }
    }

    private fun setupPlaylistBottomSheet() {
        playlistAdapter = PlaylistAdapter(
            onPlaylistClick = { playlist ->
                audioPlayerViewModel.addTrackToPlaylist(playlist)
            }, isInBottomSheet = true
        )
        audioPlayerViewModel.playlistActionStatus.observe(this) { (status, playlistName) ->
            val message = when (status) {
                PlaylistTrackStatus.ALREADY_ADDED -> getString(
                    R.string.already_added_track_in_playlist,
                    playlistName
                )

                PlaylistTrackStatus.ADDED -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    getString(R.string.added_track_to_playlist, playlistName)
                }
            }
            MaterialAlertDialogBuilder(this, R.style.CustomDialogTheme)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.playlistRecyclerView.adapter = playlistAdapter

        audioPlayerViewModel.playlists.observe(this) { playlists ->
            playlistAdapter.setPlaylists(playlists)
        }

        binding.createPlaylistButton.setOnClickListener {
            binding.fragmentContainer.visibility = View.VISIBLE

            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    CreatePlaylistFragment.newInstance(isFromActivity = true)
                )
                .addToBackStack(null)
                .commit()
        }
    }

    fun setActivityVisibility(visible: Boolean) {
        binding.audioPlayerContent.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                binding.overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun updateBottomSheetState() {
        when (bottomSheetBehavior.state) {
            BottomSheetBehavior.STATE_HIDDEN -> bottomSheetBehavior.state =
                BottomSheetBehavior.STATE_COLLAPSED

            else -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    companion object {
        const val DATA_TRACK = "trackData"
        const val TRACK_DURATION = "00:00"
        const val RADIUS = 8
    }
}