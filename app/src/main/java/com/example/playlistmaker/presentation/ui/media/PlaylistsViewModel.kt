package com.example.playlistmaker.presentation.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel(
    private val testData: Boolean
) : ViewModel() {
    private val playlistLiveData = MutableLiveData(testData)
    fun getPlaylistLiveData(): LiveData<Boolean> = playlistLiveData

}