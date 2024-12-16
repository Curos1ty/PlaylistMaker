package com.example.playlistmaker.domain.repository

import android.net.Uri

interface FileStorage {
    fun saveImage(uri: Uri): String?
}