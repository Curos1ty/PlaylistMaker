package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track

interface SearchInteractor {
    suspend fun searchSongs(query: String): List<Track>
    fun saveSearchHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()

}