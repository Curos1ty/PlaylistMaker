package com.example.playlistmaker.domain.interactor

import android.net.Uri
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean
    suspend fun savePlaylist(playlist: Playlist): Result<Unit>
    fun getImagePath(uri: Uri): String?
}