package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistTrackDao
import com.example.playlistmaker.data.db.PlaylistTrackEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val trackDao: PlaylistTrackDao
) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(PlaylistEntity.fromDomain(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(PlaylistEntity.fromDomain(playlist))
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return playlistDao.getPlaylistById(id)?.toDomain()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        trackDao.insertTrack(PlaylistTrackEntity.fromDomain(track))
        val updatedTrackIds = playlist.trackIds.toMutableList()
        updatedTrackIds.add(track.trackId)
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = playlist.trackCount + 1
        )
        playlistDao.updatePlaylist(PlaylistEntity.fromDomain(updatedPlaylist))
    }
}