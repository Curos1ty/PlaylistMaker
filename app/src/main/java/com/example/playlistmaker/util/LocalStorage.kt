package com.example.playlistmaker.util

import android.content.SharedPreferences

class LocalStorage(private val sharedPreferences: SharedPreferences) {
    companion object {
        private const val FAVORITES_KEY = "FAVORITES_KEY"
    }

    fun addToFavorites(trackId: Long) {
        changeFavorites(trackId = trackId, remove = false)
    }

    fun removeFromFavorites(trackId: Long) {
        changeFavorites(trackId = trackId, remove = true)
    }

    fun getSavedFavorites(): Set<Long> {
        val stringSet = sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
        return stringSet.map { it.toLong() }.toSet()
    }

    private fun changeFavorites(trackId: Long, remove: Boolean) {
        val mutableSet = getSavedFavorites().map { it.toString() }.toMutableSet()
        val modified =
            if (remove) mutableSet.remove(trackId.toString()) else mutableSet.add(trackId.toString())
        if (modified) sharedPreferences.edit().putStringSet(FAVORITES_KEY, mutableSet).apply()
    }
}