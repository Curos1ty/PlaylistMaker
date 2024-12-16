package com.example.playlistmaker.presentation.ui.media

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    var currentPlaylistName: String = ""
    var currentPlaylistDescription: String = ""
    var currentPlaylistImagePath: String? = null

    fun setName(name: String) {
        currentPlaylistName = name
    }

    fun setDescription(description: String) {
        currentPlaylistDescription = description
    }

    fun setImagePath(uri: Uri) {
        currentPlaylistImagePath = playlistInteractor.getImagePath(uri)
    }

    fun savePlaylist(onSuccess: () -> Unit, onError: (SavePlaylistErrorType) -> Unit) {
        if (currentPlaylistName.isBlank()) {
            onError(SavePlaylistErrorType.EMPTY_NAME_ERROR)
            return
        }

        val playlist = Playlist(
            name = currentPlaylistName,
            description = currentPlaylistDescription,
            coverImageUri = currentPlaylistImagePath,
            trackIds = emptyList(),
            trackCount = 0
        )

        viewModelScope.launch {
            playlistInteractor.savePlaylist(playlist)
                .onSuccess { onSuccess() }
                .onFailure { onError(SavePlaylistErrorType.SAVE_ERROR) }
        }
    }
}