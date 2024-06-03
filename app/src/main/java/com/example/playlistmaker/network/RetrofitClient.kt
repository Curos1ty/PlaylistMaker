package com.example.playlistmaker.network

import com.example.playlistmaker.api.ITunesApi
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.create

object RetrofitClient {
    private const val BASE_URL = "https://itunes.apple.com"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val iTunesApiService: ITunesApi = retrofit.create<ITunesApi>()
}