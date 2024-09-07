package com.example.playlistmaker.data.settings.impl

import android.content.SharedPreferences
import com.example.playlistmaker.domain.repository.ThemeSettingsRepository

class ThemeSettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeSettingsRepository {
    override fun getThemeSettings(): Boolean {
        return sharedPreferences.getBoolean("dark_theme", false)
    }

    override fun updateThemeSettings(settings: Boolean) {
        sharedPreferences.edit().putBoolean("dark_theme", settings).apply()
    }
}