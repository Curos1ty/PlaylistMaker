package com.example.playlistmaker.di

import com.example.playlistmaker.domain.interactor.FavoritesInteractor
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.interactor.ThemeSettingsInteractor
import com.example.playlistmaker.domain.interactor.ThemeSettingsInteractorImpl
import com.example.playlistmaker.domain.interactor.impl.FavoritesInteractorImpl
import com.example.playlistmaker.domain.interactor.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<SearchInteractor> { SearchInteractorImpl(get()) }
    factory<FavoritesInteractor> { FavoritesInteractorImpl(get()) }
    factory<SharingInteractor> { SharingInteractorImpl(get(), get()) }
    factory<ThemeSettingsInteractor> { ThemeSettingsInteractorImpl(get()) }
}