package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.UnknownHostException

class TrackRepositoryImpl(
    private val api: ITunesApi,
    private val searchHistory: SearchHistory
) : TrackRepository {

    override suspend fun searchSongs(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val response = api.searchSongs(query)
            val trackDtos = response.results
            if (trackDtos.isNotEmpty()) {
                emit(Result.Success(trackDtos.map { TrackCreator.map(it) }))
            } else {
                emit(Result.Success(emptyList()))
            }

        } catch (e: UnknownHostException) {
            emit(Result.NetworkError)
        } catch (e: Exception) {
            emit(Result.Error(e))
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