package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.ui.AudioPlayerViewModel
import com.example.playlistmaker.presentation.ui.media.CreatePlaylistViewModel
import com.example.playlistmaker.presentation.ui.media.FavoritesTracksViewModel
import com.example.playlistmaker.presentation.ui.media.PlaylistsViewModel
import com.example.playlistmaker.presentation.ui.search.SearchViewModel
import com.example.playlistmaker.presentation.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::SearchViewModel)
    viewModelOf(::AudioPlayerViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::PlaylistsViewModel)
    viewModelOf(::FavoritesTracksViewModel)
    viewModelOf(::CreatePlaylistViewModel)
}
