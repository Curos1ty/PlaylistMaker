package com.example.playlistmaker.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Playlist

class PlaylistAdapter(
    private val onPlaylistClick: ((Playlist) -> Unit)? = null,
    private val isInBottomSheet: Boolean = false,
    private val cornerRadius: Int
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    fun setPlaylists(playlists: List<Playlist>) {
        this.playlists = playlists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutId = if (isInBottomSheet) {
            R.layout.item_playlist
        } else {
            R.layout.item_playlist_fragment
        }
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return PlaylistViewHolder(itemView, cornerRadius)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)

        if (onPlaylistClick != null) {
            holder.itemView.setOnClickListener {
                if (!isInBottomSheet) {
                    onPlaylistClick?.let { it1 -> it1(playlist) }
                }
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
        holder.itemView.setOnClickListener { onPlaylistClick?.let { it(playlist) } }

    }
}


