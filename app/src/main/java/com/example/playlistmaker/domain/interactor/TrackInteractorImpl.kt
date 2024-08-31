package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.repository.TrackRepository
import com.example.playlistmaker.domain.model.Track

class TrackInteractorImpl(
    private val repository: TrackRepository
) : TrackInteractor {
    override suspend fun searchSongs(query: String): List<Track> {
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