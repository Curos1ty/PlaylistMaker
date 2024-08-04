package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.data.model.TrackDto
import com.example.playlistmaker.data.model.TrackSearchResponse
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.domain.model.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackRepositoryImpl(
    private val api: ITunesApi
) : TrackRepository {

    override fun searchSongs(query: String, callback: (List<Track>) -> Unit) {
        api.searchSongs(query).enqueue(object : Callback<TrackSearchResponse> {
            override fun onResponse(
                call: Call<TrackSearchResponse>,
                response: Response<TrackSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val trackDtos = response.body()?.results ?: emptyList()
                    val tracks = trackDtos.map { TrackCreator.map(it) }
                    callback(tracks)
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
                callback(emptyList())
            }
        })
    }
}