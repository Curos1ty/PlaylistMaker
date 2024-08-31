package com.example.playlistmaker.data.model

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)
