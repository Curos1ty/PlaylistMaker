package com.example.playlistmaker.data.repository

import com.example.playlistmaker.domain.model.Track

interface TrackRepository {
    fun searchSongs(query:String, callback: (List<Track>) -> Unit)
}