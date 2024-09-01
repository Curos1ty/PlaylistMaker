package com.example.playlistmaker.data.creator

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.repository.FavoritesRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepository
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.settings.impl.ThemeSettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.interactor.ThemeSettingsInteractor
import com.example.playlistmaker.domain.interactor.ThemeSettingsInteractorImpl
import com.example.playlistmaker.domain.interactor.impl.FavoritesInteractorImpl
import com.example.playlistmaker.domain.interactor.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.example.playlistmaker.util.LocalStorage

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

    private fun provideSearchHistory(context: Context): SearchHistory {
        return SearchHistory(context)
    }

    private fun provideTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(provideITunesApi(), provideSearchHistory(context))
    }

    fun provideTrackInteractor(context: Context): SearchInteractor {
        val repository = provideTrackRepository(context)
        return SearchInteractorImpl(repository)
    }


    fun provideSharingInteractor(context: Context): SharingInteractor {
        val externalNavigator = ExternalNavigatorImpl(context)
        return SharingInteractorImpl(externalNavigator, context)
    }

    fun provideFavoritesInteractor(context: Context): FavoritesInteractor {
        val localStorage =
            LocalStorage(context.getSharedPreferences("local_storage", Context.MODE_PRIVATE))
        val repository = FavoritesRepositoryImpl(localStorage)
        return FavoritesInteractorImpl(repository)
    }
}