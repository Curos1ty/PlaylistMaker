package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import com.example.playlistmaker.util.Result

interface SearchInteractor {
    suspend fun searchSongs(query: String): Flow<Result<List<Track>>>
    fun saveSearchHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()

}