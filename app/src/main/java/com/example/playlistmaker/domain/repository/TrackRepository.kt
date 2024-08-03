package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.data.model.TrackSearchResponse
import retrofit2.Call

interface TrackRepository {
    fun searchSongs(query:String): Call<TrackSearchResponse>
}