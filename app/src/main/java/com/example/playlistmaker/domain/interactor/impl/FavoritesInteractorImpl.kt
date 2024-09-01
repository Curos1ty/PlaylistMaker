package com.example.playlistmaker.domain.interactor.impl

import com.example.playlistmaker.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoritesRepository

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {
    override fun addTrackToFavorites(track: Track) {
        repository.addTrackToFavorites(track)
    }

    override fun removeTrackFromFavorites(track: Track) {
        repository.removeTrackFromFavorites(track)
    }

    override fun isTrackFavorite(trackId: Long): Boolean {
        return repository.isTrackFavorite(trackId)
    }
}