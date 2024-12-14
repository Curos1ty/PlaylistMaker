package com.example.playlistmaker.presentation.ui.media

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.data.repository.PlaylistRepository
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {
    private val _imageScaleType = MutableLiveData<ImageView.ScaleType>()
    val imageScaleType: LiveData<ImageView.ScaleType> = _imageScaleType

    private val _titleBorderColor = MutableLiveData<Int>()
    val titleBorderColor: LiveData<Int> = _titleBorderColor

    private val _descriptionBorderColor = MutableLiveData<Int>()
    val descriptionBorderColor: LiveData<Int> = _descriptionBorderColor

    var currentPlaylistName: String = ""
    var currentPlaylistDescription: String = ""
    var currentPlaylistImagePath: String? = null

    fun setName(name: String) {
        currentPlaylistName = name
        _titleBorderColor.value = if (name.isNotBlank()) R.color.blue_background else R.color.gray
    }

    fun setDescription(description: String) {
        currentPlaylistDescription = description
        _descriptionBorderColor.value =
            if (description.isNotBlank()) R.color.blue_background else R.color.gray
    }

    fun setImagePath(imagePath: String?) {
        _imageScaleType.value = ImageView.ScaleType.CENTER_CROP
        currentPlaylistImagePath = imagePath
    }

    fun savePlaylist(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (currentPlaylistName.isBlank()) {
            onError("Название плейлиста не может быть пустым")
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
                onError(e.message ?: "Ошибка сохранения плейлиста")
            }
        }
    }
}