package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface FavoritesRepository {
    fun addTrackToFavorites(track: Track)
    fun removeTrackFromFavorites(track: Track)
    fun isTrackFavorite(trackId: Long): Boolean
}