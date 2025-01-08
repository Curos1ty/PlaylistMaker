package com.example.playlistmaker.util

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        applySavedTheme()
        startKoin {
            androidContext(this@App)
            printLogger()
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
    }

    private fun applySavedTheme() {
        val sharedPreferences = getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        val isDarkTheme = sharedPreferences.getBoolean(DARK_THEME_KEY, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        val sharedPreferences = getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            apply()
        }
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val THEME_PREFS = "theme_prefs"
        const val DARK_THEME_KEY = "dark_theme"
    }
}