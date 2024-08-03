package com.example.playlistmaker.presentation.adapter

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.AudioPlayer
import com.example.playlistmaker.AudioPlayer.Companion.DATA_TRACK
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchHistory
import com.example.playlistmaker.data.model.Track

class TrackAdapter(
    private val trackList: MutableList<Track>,
    private val searchHistory: SearchHistory,
    private val isSearchHistory: Boolean = false
) : RecyclerView.Adapter<TrackViewHolder>() {

    companion object {
        private const val DEBOUNCE_DELAY = 1000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private var clickRunnable: Runnable? = null
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
            clickRunnable?.let {handler.removeCallbacks(it)}
            clickRunnable = Runnable {
                val context = holder.itemView.context
                val intent = Intent(context, AudioPlayer::class.java).apply {
                    putExtra(DATA_TRACK, track)
                }
                context.startActivity(intent)
                if (!isSearchHistory) {
                    searchHistory.saveHistory(track)
                }
            }
            handler.postDelayed(clickRunnable!!, DEBOUNCE_DELAY)
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}