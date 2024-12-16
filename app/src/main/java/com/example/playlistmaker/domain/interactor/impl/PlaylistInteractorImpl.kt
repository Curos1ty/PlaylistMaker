package com.example.playlistmaker.domain.interactor.impl

import android.net.Uri
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.FileStorage
import com.example.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository,
    private val fileStorage: FileStorage
) : PlaylistInteractor {
    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        return if (playlist.trackIds.contains(track.trackId)) {
            true
        } else {
            repository.addTrackToPlaylist(track, playlist)
            false
        }
    }

    override suspend fun savePlaylist(playlist: Playlist): Result<Unit> {
        return try {
            repository.addPlaylist(playlist)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getImagePath(uri: Uri): String? {
        return fileStorage.saveImage(uri)
    }
}