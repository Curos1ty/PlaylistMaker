package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(playlistTrackEntity: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun getTracksByIds(playlistId: Long): List<PlaylistTrackEntity>

    @Query("DELETE FROM PLAYLIST_TRACKS WHERE trackId = :trackId AND playlistId = :playlistId")
    suspend fun deleteTrackById(trackId: Long, playlistId: Long)

    @Query("SELECT playlistId FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun getPlaylistIdsByTrackId(trackId: Long): List<Long>

}