package com.example.playlistmaker.data.model

import com.example.playlistmaker.data.model.Track

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<Track>
)
