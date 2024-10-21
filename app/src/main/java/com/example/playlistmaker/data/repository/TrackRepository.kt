package com.example.playlistmaker.data.repository

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow
import com.example.playlistmaker.util.Result

interface TrackRepository {
    suspend fun searchSongs(query: String): Flow<Result<List<Track>>>
    fun saveTrack(track: Track)
    fun getTrackHistory(): List<Track>

    fun clearHistory()
}