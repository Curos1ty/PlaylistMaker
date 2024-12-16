package com.example.playlistmaker.presentation.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val _playlistsLiveData = MutableLiveData<List<Playlist>>()
    val playlistsLiveData: LiveData<List<Playlist>> get() = _playlistsLiveData

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                _playlistsLiveData.value = playlists
            }
        }
    }
}