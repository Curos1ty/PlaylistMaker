package com.example.playlistmaker.domain.repository

interface ThemeSettingsRepository {
    fun getThemeSettings(): Boolean
    fun saveThemeSettings(isDarkTheme: Boolean)
}