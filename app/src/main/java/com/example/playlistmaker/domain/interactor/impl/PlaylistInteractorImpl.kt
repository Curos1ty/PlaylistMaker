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

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return repository.getPlaylistById(id)
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        return repository.getTracksForPlaylist(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long) {
        repository.deleteTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun deletePlaylist(id: Long) {
        repository.deletePlaylistById(id)
    }
}