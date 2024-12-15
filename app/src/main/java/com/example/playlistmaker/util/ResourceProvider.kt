package com.example.playlistmaker.util

interface ResourceProvider {
    fun getString(resId: Int): String
}