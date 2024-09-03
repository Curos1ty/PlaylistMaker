package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.ThemeSettingsRepository

class ThemeSettingsInteractorImpl(
    private val repository: ThemeSettingsRepository
) : ThemeSettingsInteractor {
    override fun getThemeSettings(): Boolean {
        return repository.getThemeSettings()
    }

    override fun setDarkThemeEnabled(isEnabled: Boolean) {
        repository.updateThemeSettings(isEnabled)
    }
}