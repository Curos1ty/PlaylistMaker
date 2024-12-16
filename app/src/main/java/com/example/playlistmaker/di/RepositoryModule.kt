package com.example.playlistmaker.di

import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.data.repository.FavoritesRepositoryImpl
import com.example.playlistmaker.data.repository.PlaylistRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepository
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.data.settings.impl.ThemeSettingsRepositoryImpl
import com.example.playlistmaker.domain.db.FavoritesRepository
import com.example.playlistmaker.domain.repository.PlaylistRepository
import com.example.playlistmaker.domain.repository.ThemeSettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { TrackCreator }
    single<TrackRepository> { TrackRepositoryImpl(get(), get()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get(), get()) }
    single<ThemeSettingsRepository> { ThemeSettingsRepositoryImpl(get()) }
    single<PlaylistRepository> { PlaylistRepositoryImpl(get(), get()) }
}