package com.example.playlistmaker.domain.interactor

interface ThemeSettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(isEnabled: Boolean)
}