package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun getAllFavoritesTrack(): Flow<List<Track>>
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(track: Track)
    suspend fun isTrackFavorite(trackId: Long): Boolean
}