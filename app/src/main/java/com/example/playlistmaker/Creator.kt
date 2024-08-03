package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.repository.ThemeSettingsRepositoryImpl
import com.example.playlistmaker.domain.interactor.ThemeSettingsInteractor
import com.example.playlistmaker.domain.interactor.ThemeSettingsInteractorImpl

object Creator {
    private fun provideSharedPreferences(context: Context): SharedPreferences{
        return context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    }
    fun provideThemeSettingsInteractor(context: Context): ThemeSettingsInteractor {
        val repository = ThemeSettingsRepositoryImpl(provideSharedPreferences(context))
        return ThemeSettingsInteractorImpl(repository)
    }
}