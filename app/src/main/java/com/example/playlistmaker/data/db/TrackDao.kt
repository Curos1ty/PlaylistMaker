package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToFavorites(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun removeTrackFromFavorites(trackEntity: TrackEntity)

    @Query("SELECT * FROM favorite_tracks where inFavorites=true order by addedDate DESC")
    fun getAllFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getFavoriteTrackId(): List<Long>

}