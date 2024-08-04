package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.data.repository.TrackRepository
import com.example.playlistmaker.domain.model.Track

class TrackInteractorImpl(
    private val repository: TrackRepository,
    private val searchHistory: SearchHistory
) : TrackInteractor {
    override fun searchSongs(query: String, callback: (List<Track>) -> Unit) {
        repository.searchSongs(query, callback)
    }

    override fun saveSearchHistory(track: Track) {
        val trackDto = TrackCreator.map(track)
        searchHistory.saveHistory(trackDto)
    }

    override fun getSearchHistory(): List<Track> {
        val trackDtos = searchHistory.getHistory()
        return trackDtos.map { TrackCreator.map(it) }
    }

    override fun clearSearchHistory() {
        searchHistory.clearHistory()
    }

}