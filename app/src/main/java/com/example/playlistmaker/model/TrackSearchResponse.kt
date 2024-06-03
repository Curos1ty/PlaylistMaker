package com.example.playlistmaker.model

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<Track>
)
