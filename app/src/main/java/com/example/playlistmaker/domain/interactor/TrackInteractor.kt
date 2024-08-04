package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track

interface TrackInteractor {
    fun searchSongs(query: String, callback: (List<Track>) -> Unit)
    fun saveSearchHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()
}