package com.example.playlistmaker.presentation.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.util.getCorrectForm
import com.example.playlistmaker.util.toPx

class PlaylistViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cornerRadiusValue = 2

    private val playlistNameTextView: TextView = itemView.findViewById(R.id.playlistTitle)
    private val playlistNumberTracksPlaylist: TextView = itemView.findViewById(R.id.numberTracksPlaylist)
    private val playlistCoverImageView: ImageView = itemView.findViewById(R.id.playlistImage)

    fun bind(playlist: Playlist) {
        playlistNameTextView.text = playlist.name
        val trackCountText = "${playlist.trackCount} ${getCorrectForm(playlist.trackCount, listOf("трек", "трека", "треков"))}"
        playlistNumberTracksPlaylist.text = trackCountText

        Glide.with(itemView)
            .load(playlist.coverImageUri)
            .placeholder(R.drawable.placeholder_music)
            .transform(RoundedCorners(itemView.context.toPx(cornerRadiusValue).toInt()))
            .into(playlistCoverImageView)
    }
}