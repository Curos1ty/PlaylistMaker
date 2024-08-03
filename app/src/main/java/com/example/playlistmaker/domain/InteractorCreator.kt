package com.example.playlistmaker.domain

import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.interactor.TrackInteractor
import com.example.playlistmaker.domain.interactor.TrackInteractorImpl

object InteractorCreator {
    fun createTrackInteractor(api: ITunesApi): TrackInteractor {
        val repository = TrackRepositoryImpl(api)
        return TrackInteractorImpl(repository)
    }
}