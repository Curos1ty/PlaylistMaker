package com.example.playlistmaker.domain.repository

interface ThemeSettingsRepository {
    fun getThemeSettings(): Boolean
    fun updateThemeSettings(settings: Boolean)
}