package com.example.playlistmaker.data.repository

import android.util.Log
import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistTrackDao
import com.example.playlistmaker.data.db.PlaylistTrackEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistRepository
import com.google.gson.Gson
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
        trackDao.insertTrack(PlaylistTrackEntity.fromDomain(track, playlist.id))

        val updatedTrackIds = playlist.trackIds.toMutableList()
        if (!updatedTrackIds.contains(track.trackId)) {
            updatedTrackIds.add(track.trackId)
        }
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )
        playlistDao.updatePlaylist(PlaylistEntity.fromDomain(updatedPlaylist))
    }

    override suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        return trackDao.getTracksByIds(playlistId).map { it.toDomain() }
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long) {
        trackDao.deleteTrackById(trackId, playlistId)
        val playlistEntity = playlistDao.getPlaylistById(playlistId)

        playlistEntity?.let {
            val trackIdsList = Gson().fromJson(it.trackIds, Array<Long>::class.java).toMutableList()
            trackIdsList.remove(trackId)

            val newTrackCount = trackIdsList.size
            playlistDao.updatePlaylist(
                it.copy(
                    trackIds = Gson().toJson(trackIdsList),
                    trackCount = newTrackCount
                )
            )
        }

        val playlistIdsHaveTrack = trackDao.getPlaylistIdsByTrackId(trackId)
        if (playlistIdsHaveTrack.isEmpty()) {
            trackDao.deleteTrackById(trackId, playlistId)
        }

        Log.d("deleteLog", playlistIdsHaveTrack.toString())

    }

    override suspend fun deletePlaylistById(id: Long) {
        playlistDao.deletePlaylistById(id)
    }
}