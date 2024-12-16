package com.example.playlistmaker.domain.interactor.impl

import com.example.playlistmaker.data.repository.TrackRepository
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.Result
import kotlinx.coroutines.flow.Flow

class SearchInteractorImpl(
    private val repository: TrackRepository
) : SearchInteractor {
    override suspend fun searchSongs(query: String): Flow<Result<List<Track>>> {
        return repository.searchSongs(query)
    }

    override fun saveSearchHistory(track: Track) {
        repository.saveTrack(track)
    }

    override fun getSearchHistory(): List<Track> {
        return repository.getTrackHistory()
    }

    override fun clearSearchHistory() {
        repository.clearHistory()
    }
}