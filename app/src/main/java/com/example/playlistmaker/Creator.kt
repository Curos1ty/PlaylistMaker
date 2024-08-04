package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.repository.ThemeSettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepository
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.interactor.ThemeSettingsInteractor
import com.example.playlistmaker.domain.interactor.ThemeSettingsInteractorImpl
import com.example.playlistmaker.domain.interactor.TrackInteractor
import com.example.playlistmaker.domain.interactor.TrackInteractorImpl

object Creator {
    private fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
    }

    fun provideThemeSettingsInteractor(context: Context): ThemeSettingsInteractor {
        val repository = ThemeSettingsRepositoryImpl(provideSharedPreferences(context))
        return ThemeSettingsInteractorImpl(repository)
    }

    private fun provideITunesApi(): ITunesApi {
        return RetrofitClient.iTunesApiService
    }

    private fun provideTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(provideITunesApi())
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        val repository = provideTrackRepository()
        val searchHistory = SearchHistory(context)
        return TrackInteractorImpl(repository, searchHistory)
    }
}