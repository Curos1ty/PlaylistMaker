package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.repository.ThemeSettingsRepository

class ThemeSettingsRepositoryImpl (
    private val sharedPreferences: SharedPreferences
) : ThemeSettingsRepository {
    override fun getThemeSettings(): Boolean {
        return sharedPreferences.getBoolean("dark_theme", false)
    }

    override fun saveThemeSettings(isDarkTheme: Boolean) {
        sharedPreferences.edit().putBoolean("dark_theme", isDarkTheme).apply()
    }
}