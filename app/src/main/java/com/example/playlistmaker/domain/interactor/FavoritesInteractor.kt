package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track

interface FavoritesInteractor {
    fun addTrackToFavorites(track: Track)
    fun removeTrackFromFavorites(track: Track)
    fun isTrackFavorite(trackId: Long): Boolean
}