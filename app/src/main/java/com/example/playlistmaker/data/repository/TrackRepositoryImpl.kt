package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TrackRepositoryImpl(
    private val api: ITunesApi,
    private val searchHistory: SearchHistory
) : TrackRepository {

    override suspend fun searchSongs(query: String): Flow<List<Track>> = flow<List<Track>> {
        try {
            val response = api.searchSongs(query)
            val trackDtos = response.results
            emit(trackDtos.map { TrackCreator.map(it) })
            emit(emptyList())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

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