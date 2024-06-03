package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.model.Track

class TrackAdapter(
    private val trackList: MutableList<Track>,
    private val searchHistory: SearchHistory,
    private val isSearchHistory: Boolean = false
) : RecyclerView.Adapter<TrackViewHolder>() {

    fun updateData(newTrackList: List<Track>) {
        trackList.clear()
        trackList.addAll(newTrackList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            if (!isSearchHistory) {
                searchHistory.saveHistory(track)
            }

        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}