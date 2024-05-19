package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class SearchActivity : AppCompatActivity() {
    private var searchText: String = ""

    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val trackList = ArrayList<Track>()
        trackList.add(
            Track(
                trackName = "Smells Like Teen Spirit",
                artistName = "Nirvana",
                trackTime = "5:01",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            )
        )
        trackList.add(
            Track(
                trackName = "Billie Jean",
                artistName = "Michael Jackson",
                trackTime = "4:35",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            )
        )
        trackList.add(
            Track(
                trackName = "Stayin' Alive",
                artistName = "Bee Gees",
                trackTime = "4:10",
                artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            )
        )
        trackList.add(
            Track(
                trackName = "Whole Lotta Love",
                artistName = "Led Zeppelin",
                trackTime = "5:33",
                artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            )
        )
        trackList.add(
            Track(
                trackName = "Sweet Child O'Mine",
                artistName = "Guns N' Roses",
                trackTime = "5:03",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val inputEditTextSearch = findViewById<AppCompatEditText>(R.id.inputEditTextSearch)
        val clearButton = findViewById<ImageButton>(R.id.clear_search_button_icon)

        inputEditTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        clearButton.setOnClickListener {
            inputEditTextSearch.setText("")
            inputEditTextSearch.hideKeyboard()
            inputEditTextSearch.clearFocus()
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            inputEditTextSearch.setText(searchText)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
    }

}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String
)

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameTextView: TextView = itemView.findViewById(R.id.songTitle)
    private val artistNameTextView: TextView = itemView.findViewById(R.id.songArtist)
    private val artworkImageView: ImageView = itemView.findViewById(R.id.songImage)

    fun bind(track: Track) {
        trackNameTextView.text = track.trackName
        val separator = "\u2022"
        artistNameTextView.text = "${track.artistName} $separator ${track.trackTime}"

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.settings)
            .transform(RoundedCorners(2))
            .into(artworkImageView)


    }
}

class TrackAdapter(private val trackList: List<Track>) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}
