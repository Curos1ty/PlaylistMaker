package com.example.playlistmaker.data.repository

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FavoritesRepository
import com.example.playlistmaker.util.LocalStorage

class FavoritesRepositoryImpl(
    private val localStorage: LocalStorage
) : FavoritesRepository {

    override fun addTrackToFavorites(track: Track) {
        localStorage.addToFavorites(track.trackId)
    }

    override fun removeTrackFromFavorites(track: Track) {
        localStorage.removeFromFavorites(track.trackId)
    }

    override fun isTrackFavorite(trackId: Long): Boolean {
        return localStorage.getSavedFavorites().contains(trackId)
    }
}