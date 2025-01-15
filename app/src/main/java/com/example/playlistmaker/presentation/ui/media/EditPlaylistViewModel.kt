package com.example.playlistmaker.presentation.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : CreatePlaylistViewModel(playlistInteractor) {

    private val _playlistId = MutableLiveData<Long>()
    val playlistId: LiveData<Long> get() = _playlistId

    private var existingTrackIds: List<Long> = emptyList()
    private var existingTrackCount: Int = 0

    fun setPlaylistId(id: Long) {
        _playlistId.value = id
        loadPlaylistInfo(id)
    }

    fun savePlaylist() {
        viewModelScope.launch {
            val playlist = Playlist(
                id = _playlistId.value ?: 0,
                name = _currentPlaylistName.value ?: "",
                description = _currentPlaylistDescription.value ?: "",
                coverImageUri = _currentPlaylistImagePath.value,
                trackIds = existingTrackIds,
                trackCount = existingTrackCount
            )
            playlistInteractor.updatePlaylist(playlist)
        }
    }

    fun loadPlaylistInfo(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            _currentPlaylistName.value = playlist?.name
            _currentPlaylistDescription.value = playlist?.description
            _currentPlaylistImagePath.value = playlist?.coverImageUri
            existingTrackIds = playlist?.trackIds!!
            existingTrackCount = playlist.trackCount
        }
    }

}