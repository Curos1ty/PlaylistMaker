package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.model.Track

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cornerRadiusValue = 2
    companion object {
        private const val SEPARATOR = "\u2022"
    }
    private val trackNameTextView: TextView = itemView.findViewById(R.id.songTitle)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.songArtist)
    private val artworkImageView: ImageView = itemView.findViewById(R.id.songImage)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = "${track.artistName} $SEPARATOR ${track.trackTime}"

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder_music)
            .transform(RoundedCorners(itemView.context.toPx(cornerRadiusValue).toInt()))
            .into(artworkImageView)


    }
}