package com.example.playlistmaker.domain.interactor.impl

import com.example.playlistmaker.domain.db.FavoritesRepository
import com.example.playlistmaker.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {
    override suspend fun getAllFavoritesTracks(): Flow<List<Track>> {
        return repository.getAllFavoritesTrack().map { tracks ->
            tracks.sortedBy { it.addedDate }
        }
    }

    override suspend fun addTrackToFavorites(track: Track) {
        repository.addTrackToFavorites(track)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        repository.removeTrackFromFavorites(track)
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return repository.isTrackFavorite(trackId)
    }
}