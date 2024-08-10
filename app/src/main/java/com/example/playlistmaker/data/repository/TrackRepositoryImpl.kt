package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepositoryImpl(
    private val api: ITunesApi,
    private val searchHistory: SearchHistory
) : TrackRepository {

    override suspend fun searchSongs(query: String): List<Track> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.searchSongs(query).execute()
                if (response.isSuccessful) {
                    val trackDtos = response.body()?.results ?: emptyList()
                    trackDtos.map { TrackCreator.map(it) }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    override fun saveTrack(track: Track) {
        searchHistory.saveHistory(track)
    }

    override fun getTrackHistory(): List<Track> {
        val trackDtos = searchHistory.getHistory()
        return trackDtos.map { TrackCreator.map(it) }
    }

    override fun clearHistory() {
        searchHistory.clearHistory()
    }
}