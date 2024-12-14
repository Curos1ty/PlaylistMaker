package com.example.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.domain.model.Track

@Entity(tableName = "playlist_tracks")
data class PlaylistTrackEntity(
    @PrimaryKey val trackId: Long,
    val trackName: String,
    val artistName: String,
    val albumName: String?,
    val trackTimeMillis: Long,
    val artworkUrl: String?,
    val previewUrl: String?,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null
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
        fun fromDomain(track: Track): PlaylistTrackEntity {
            return PlaylistTrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                albumName = track.collectionName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl = track.artworkUrl512,
                previewUrl = track.previewUrl
            )
        }
    }
}

