package com.example.playlistmaker.di

import com.example.playlistmaker.data.repository.FavoritesRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepository
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.settings.impl.ThemeSettingsRepositoryImpl
import com.example.playlistmaker.domain.repository.FavoritesRepository
import com.example.playlistmaker.domain.repository.ThemeSettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> { TrackRepositoryImpl(get(), get()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get()) }
    single<ThemeSettingsRepository> { ThemeSettingsRepositoryImpl(get()) }
}