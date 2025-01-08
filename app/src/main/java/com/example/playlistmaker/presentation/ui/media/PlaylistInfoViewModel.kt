package com.example.playlistmaker.presentation.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.getCorrectForm
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist?>()
    val playlist: LiveData<Playlist?> get() = _playlist

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _totalDuration = MutableLiveData<String>()
    val totalDuration: LiveData<String> get() = _totalDuration

    private val _tracksText = MutableLiveData<String>()
    val tracksText: LiveData<String> get() = _tracksText


    fun loadPlaylistInfo(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            _playlist.value = playlist

            if (playlist != null) {
                val trackList = playlistInteractor.getTracksForPlaylist(playlistId)
                _tracks.value = trackList.toList()
                updateTextFields(trackList)
            }
        }
    }

    private fun updateTextFields(tracks: List<Track>) {
        val totalDurationMinutes = tracks.sumOf { (it.trackTimeMillis ?: 0) / 60000 }.toInt()
        val trackCount = tracks.size

        val durationText = "$totalDurationMinutes ${
            getCorrectForm(
                totalDurationMinutes,
                listOf("минута", "минуты", "минут")
            )
        }"
        val tracksText =
            "$trackCount ${getCorrectForm(trackCount, listOf("трек", "трека", "треков"))}"

        _totalDuration.value = durationText
        _tracksText.value = tracksText
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        viewModelScope.launch {
            val currentPlaylist = _playlist.value
            if (currentPlaylist != null) {
                playlistInteractor.deleteTrackFromPlaylist(trackId, currentPlaylist.id)

                _tracks.value = getUpdateTrackList()

                loadPlaylistInfo(currentPlaylist.id)
            }
        }
    }

    private suspend fun getUpdateTrackList(): List<Track> {
        val currentPlaylist = _playlist.value
        return if (currentPlaylist != null) {
            playlistInteractor.getTracksForPlaylist(currentPlaylist.id)
        } else {
            emptyList()
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(id)
        }
    }
}