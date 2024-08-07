package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.model.TrackSearchResponse
import com.example.playlistmaker.domain.interactor.TrackInteractor
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.create

object RetrofitClient{
    private val BASE_URL = "https://itunes.apple.com"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val iTunesApiService: ITunesApi = retrofit.create<ITunesApi>()
}