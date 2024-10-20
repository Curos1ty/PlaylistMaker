package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    suspend fun searchSongs(query: String): Flow<List<Track>>
    fun saveSearchHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()

}