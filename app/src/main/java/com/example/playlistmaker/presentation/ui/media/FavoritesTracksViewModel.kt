package com.example.playlistmaker.presentation.ui.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoritesTracksViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _favoriteTrackLiveData = MutableLiveData<List<Track>>()
    val favoriteTrackLiveData: LiveData<List<Track>> get() = _favoriteTrackLiveData
    init {
        loadFavoritesTracks()
    }
    private fun loadFavoritesTracks() {
        viewModelScope.launch {
            favoritesInteractor.getAllFavoritesTracks().collect() { tracks ->
                _favoriteTrackLiveData.value = tracks
            }
        }
    }
}