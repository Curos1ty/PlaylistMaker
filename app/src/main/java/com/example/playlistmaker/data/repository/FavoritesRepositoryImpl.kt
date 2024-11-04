package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.db.FavoritesRepository
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val database: AppDatabase,
    private val trackCreator: TrackCreator
) : FavoritesRepository {

    override suspend fun getAllFavoritesTrack(): Flow<List<Track>> {
        return database.trackDao().getAllFavoriteTracks().map { entities ->
            entities.map { trackCreator.mapFromEntity(it) }
        }
    }

    override suspend fun addTrackToFavorites(track: Track) {
        track.inFavorites = true
        track.addedDate = System.currentTimeMillis()
        val entity = trackCreator.mapToEntity(track)
        database.trackDao().addTrackToFavorites(entity)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        track.inFavorites = false
        val entity = trackCreator.mapToEntity(track)
        database.trackDao().removeTrackFromFavorites(entity)
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        val favoriteIds = database.trackDao().getFavoriteTrackId()
        return favoriteIds.contains(trackId)
    }
}