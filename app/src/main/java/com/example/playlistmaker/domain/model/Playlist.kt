package com.example.playlistmaker.domain.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverImageUri: String? = null,
    val trackIds: List<Long> = listOf(),
    val trackCount: Int = 0
)
