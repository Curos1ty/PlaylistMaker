package com.example.playlistmaker.presentation.ui.media

import androidx.annotation.StringRes
import com.example.playlistmaker.R

enum class SavePlaylistErrorType(@StringRes val messageResId: Int) {
    EMPTY_NAME_ERROR(R.string.text_name_playlist_cannot_be_empty),
    SAVE_ERROR(R.string.text_error_saving_playlist)
}