package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.model.TrackSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    suspend fun searchSongs(@Query("term") text: String): TrackSearchResponse
}