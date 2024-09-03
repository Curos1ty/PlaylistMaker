package com.example.playlistmaker.data.repository

import com.example.playlistmaker.domain.model.Track

interface TrackRepository {
    suspend fun searchSongs(query: String): List<Track>
    fun saveTrack(track: Track)
    fun getTrackHistory(): List<Track>

    fun clearHistory()
}