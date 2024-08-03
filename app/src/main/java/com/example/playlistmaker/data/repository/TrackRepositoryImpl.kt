package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.model.TrackSearchResponse
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.domain.repository.TrackRepository
import retrofit2.Call

class TrackRepositoryImpl(
    private val api: ITunesApi
): TrackRepository {
    override fun searchSongs(query: String): Call<TrackSearchResponse> {
        return api.searchSongs(query)
    }
}