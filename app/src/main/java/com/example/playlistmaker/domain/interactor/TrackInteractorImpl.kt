package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.data.model.TrackSearchResponse
import com.example.playlistmaker.domain.repository.TrackRepository
import retrofit2.Call

class TrackInteractorImpl(
    private val repository: TrackRepository
): TrackInteractor {
    override fun searchSongs(query: String): Call<TrackSearchResponse> {
        return repository.searchSongs(query)
    }
}