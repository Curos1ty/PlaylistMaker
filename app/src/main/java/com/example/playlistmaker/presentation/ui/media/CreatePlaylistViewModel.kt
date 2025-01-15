package com.example.playlistmaker.presentation.ui.media

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    protected val _currentPlaylistName = MutableLiveData<String>()
    val currentPlaylistName: LiveData<String> get() = _currentPlaylistName

    protected val _currentPlaylistDescription = MutableLiveData<String>()
    val currentPlaylistDescription: LiveData<String> get() = _currentPlaylistDescription

    protected val _currentPlaylistImagePath = MutableLiveData<String?>()
    val currentPlaylistImagePath: LiveData<String?> get() = _currentPlaylistImagePath

    fun setName(name: String) {
        _currentPlaylistName.value = name
    }

    fun setDescription(description: String) {
        _currentPlaylistDescription.value = description
    }

    fun setImagePath(uri: Uri) {
        _currentPlaylistImagePath.value = playlistInteractor.getImagePath(uri)
    }

    fun savePlaylist(onSuccess: () -> Unit, onError: (SavePlaylistErrorType) -> Unit) {
        if (currentPlaylistName.value.isNullOrBlank()) {
            onError(SavePlaylistErrorType.EMPTY_NAME_ERROR)
            return
        }

        val playlist = Playlist(
            name = currentPlaylistName.value ?: "",
            description = currentPlaylistDescription.value ?: "",
            coverImageUri = currentPlaylistImagePath.value,
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