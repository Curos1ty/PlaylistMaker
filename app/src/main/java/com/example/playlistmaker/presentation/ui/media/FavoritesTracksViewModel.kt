package com.example.playlistmaker.presentation.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesTracksViewModel(
    private val testData: Boolean
) : ViewModel() {
    private val favoritesTracksLiveData = MutableLiveData(testData)
    fun getFavoritesTracksLiveData(): LiveData<Boolean> = favoritesTracksLiveData
}