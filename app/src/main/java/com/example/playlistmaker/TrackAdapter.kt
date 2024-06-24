package com.example.playlistmaker

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.model.Track

class TrackAdapter(
    private val trackList: MutableList<Track>,
    private val searchHistory: SearchHistory,
    private val isSearchHistory: Boolean = false
) : RecyclerView.Adapter<TrackViewHolder>() {

    companion object {
        const val DATA_TRACK = "trackData"
    }

    fun updateData(newTrackList: List<Track>) {
        trackList.clear()
        trackList.addAll(newTrackList)
        notifyDataSetChanged()
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
            val context = holder.itemView.context
            val intent = Intent(context, AudioPlayer::class.java).apply {
                putExtra(DATA_TRACK, track)
            }
            context.startActivity(intent)
            if (!isSearchHistory) {
                searchHistory.saveHistory(track)
            }

        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}