package com.example.playlistmaker.presentation.ui.settings.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.data.creator.Creator
import com.example.playlistmaker.domain.interactor.ThemeSettingsInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeSettingsInteractor: ThemeSettingsInteractor
) : ViewModel() {

    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> get() = _isDarkTheme

    init {
        _isDarkTheme.value = themeSettingsInteractor.getThemeSettings()
    }

    fun setDarkThemeEnabled(isEnabled: Boolean) {
        themeSettingsInteractor.setDarkThemeEnabled(isEnabled)
        _isDarkTheme.value = isEnabled
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val sharingInteractor = Creator.provideSharingInteractor(context)
                val themeSettingsInteractor = Creator.provideThemeSettingsInteractor(context)

                SettingsViewModel(sharingInteractor, themeSettingsInteractor)
            }
        }
    }
}