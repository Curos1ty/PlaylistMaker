package com.example.playlistmaker.presentation.ui

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.repository.PlaylistRepository
import com.example.playlistmaker.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.TimeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val interactor: FavoritesInteractor,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _trackDuration = MutableLiveData<String>()
    val trackDuration: LiveData<String> = _trackDuration

    private val _playerState = MutableLiveData<Int>().apply { value = STATE_DEFAULT }
    val playerState: LiveData<Int> = _playerState

    private val _isPlaying = MutableLiveData<Boolean>().apply { value = false }
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _isInPlaylist = MutableLiveData<Boolean>().apply { value = false }
    val isInPlaylist: LiveData<Boolean> = _isInPlaylist

    private lateinit var currentTrack: Track

    private var mediaPlayer: MediaPlayer? = MediaPlayer()

    private var updateJob: Job? = null

    private val _bottomSheetState =
        MutableLiveData<Int>().apply { value = BottomSheetBehavior.STATE_HIDDEN }
    val bottomSheetState: LiveData<Int> = _bottomSheetState

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _playlistActionStatus = MutableLiveData<String>()
    val playlistActionStatus: LiveData<String> = _playlistActionStatus


    fun preparePlayer(url: String, track: Track) {
        currentTrack = track
        viewModelScope.launch {
            _isFavorite.value = interactor.isTrackFavorite(track.trackId)
        }


        mediaPlayer?.setDataSource(url)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            _playerState.value = STATE_PREPARED
        }
        mediaPlayer?.setOnCompletionListener {
            _playerState.value = STATE_PREPARED
            _isPlaying.value = false
            stopUpdatingTime()

            _trackDuration.value = TimeUtils.formatTime(0)
        }
    }


    fun playbackControl() {
        when (_playerState.value) {
            STATE_PLAYING -> pauseAudio()
            STATE_PREPARED, STATE_PAUSED -> playAudio()
        }
    }

    fun playAudio() {
        mediaPlayer?.start()
        _isPlaying.value = true
        _playerState.value = STATE_PLAYING
        startUpdatingTime()
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
        _isPlaying.value = false
        _playerState.value = STATE_PAUSED
        stopUpdatingTime()
    }

    fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun startUpdatingTime() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch(Dispatchers.Main) {
            while (mediaPlayer?.isPlaying == true) {
                _trackDuration.value =
                    TimeUtils.formatTime(mediaPlayer?.currentPosition?.toLong() ?: 0L)
                delay(300L)
            }
            _trackDuration.value = TimeUtils.formatTime(0)
        }
    }

    fun stopUpdatingTime() {
        updateJob?.cancel()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val isCurrentlyFavorite = _isFavorite.value
            currentTrack.inFavorites = !isCurrentlyFavorite!!

            _isFavorite.value = currentTrack.inFavorites
            if (currentTrack.inFavorites) {
                interactor.addTrackToFavorites(currentTrack)
            } else {
                interactor.removeTrackFromFavorites(currentTrack)
            }
        }
    }

    fun togglePlaylist() {
        _bottomSheetState.value =
            if (_bottomSheetState.value == BottomSheetBehavior.STATE_COLLAPSED or BottomSheetBehavior.STATE_EXPANDED) {
                BottomSheetBehavior.STATE_HIDDEN
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistRepository.getAllPlaylists().collect { playlistList ->
                _playlists.value = playlistList
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            if (playlist.trackIds.contains(currentTrack.trackId)) {
                _playlistActionStatus.value = "Трек уже добавлен в плейлист ${playlist.name}"
            } else {
                playlistRepository.addTrackToPlaylist(currentTrack, playlist)
                _isInPlaylist.value = true
                Log.d("BSheet", _bottomSheetState.value.toString())
                _bottomSheetState.value = BottomSheetBehavior.STATE_HIDDEN
                _playlistActionStatus.value = "Трек добавлен в плейлист ${playlist.name}"
            }

        }
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
    }

}