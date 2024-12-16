package com.example.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.domain.model.Playlist
import com.google.gson.Gson

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val coverImageUri: String? = null,
    val trackIds: String = "",
    val trackCount: Int = 0
) {
    fun toDomain(): Playlist {
        val trackIdList = Gson().fromJson(trackIds, Array<Long>::class.java).toList()
        return Playlist(
            id = id,
            name = name,
            description = description,
            coverImageUri = coverImageUri,
            trackIds = trackIdList,
            trackCount = trackCount
        )
    }

    companion object {
        fun fromDomain(playlist: Playlist): PlaylistEntity {
            val trackIdsJson = Gson().toJson(playlist.trackIds)
            return PlaylistEntity(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                coverImageUri = playlist.coverImageUri,
                trackIds = trackIdsJson,
                trackCount = playlist.trackCount
            )
        }
    }
}
