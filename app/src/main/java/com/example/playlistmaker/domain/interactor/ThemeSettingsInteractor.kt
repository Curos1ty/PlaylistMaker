package com.example.playlistmaker.domain.interactor

interface ThemeSettingsInteractor {
    fun getThemeSettings(): Boolean
    fun setDarkThemeEnabled(isEnabled: Boolean)
}