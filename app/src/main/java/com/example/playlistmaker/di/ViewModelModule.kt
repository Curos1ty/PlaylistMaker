package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.ui.AudioPlayerViewModel
import com.example.playlistmaker.presentation.ui.search.SearchViewModel
import com.example.playlistmaker.presentation.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SearchViewModel(get()) }
    viewModel { AudioPlayerViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
}
