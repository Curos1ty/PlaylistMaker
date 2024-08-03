package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.model.TrackSearchResponse
import retrofit2.Call

interface TrackInteractor {
    fun searchSongs(query:String): Call<TrackSearchResponse>
}