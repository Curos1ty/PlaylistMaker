package com.example.playlistmaker.presentation.ui.media

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.data.repository.PlaylistRepository
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.util.ResourceProvider
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistRepository: PlaylistRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
    private val _imageScaleType = MutableLiveData<ImageView.ScaleType>()
    val imageScaleType: LiveData<ImageView.ScaleType> = _imageScaleType

    private val _isTitleFilled = MutableLiveData<Boolean>()
    val isTitleFilled: LiveData<Boolean> = _isTitleFilled

    private val _isDescriptionFilled = MutableLiveData<Boolean>()
    val isDescriptionFilled: LiveData<Boolean> = _isDescriptionFilled

    var currentPlaylistName: String = ""
    var currentPlaylistDescription: String = ""
    var currentPlaylistImagePath: String? = null

    fun setName(name: String) {
        currentPlaylistName = name
        _isTitleFilled.value = name.isNotBlank()
    }

    fun setDescription(description: String) {
        currentPlaylistDescription = description
        _isDescriptionFilled.value = description.isNotBlank()
    }

    fun setImagePath(imagePath: String?) {
        _imageScaleType.value = ImageView.ScaleType.CENTER_CROP
        currentPlaylistImagePath = imagePath
    }

    fun savePlaylist(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (currentPlaylistName.isBlank()) {
            onError(resourceProvider.getString(R.string.text_name_playlist_cannot_be_empty))
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
            try {
                playlistRepository.addPlaylist(playlist)
                onSuccess()
            } catch (e: Exception) {
                onError(
                    e.message ?: resourceProvider.getString(R.string.text_error_saving_playlist)
                )
            }
        }
    }
}