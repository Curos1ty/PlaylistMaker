package com.example.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.playlistmaker.domain.model.Track

@Entity(
    tableName = "playlist_tracks", primaryKeys = ["trackId", "playlistId"],
    indices = [Index(value = ["playlistId"])],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class PlaylistTrackEntity(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val albumName: String?,
    val trackTimeMillis: Long,
    val artworkUrl: String?,
    val previewUrl: String?,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null,
    val playlistId: Long
) {
    fun toDomain(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            collectionName = albumName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl ?: "",
            previewUrl = previewUrl ?: "",
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            country = country
        )
    }

    companion object {
        fun fromDomain(track: Track, playlistId: Long): PlaylistTrackEntity {
            return PlaylistTrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                albumName = track.collectionName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl = track.artworkUrl512,
                previewUrl = track.previewUrl,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                playlistId = playlistId

            )
        }
    }
}

