package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.api.ITunesApi
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.model.TrackSearchResponse
import com.example.playlistmaker.network.RetrofitClient
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

class SearchActivity : AppCompatActivity() {
    private var searchText: String = ""
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var noResultsPlaceholder: View
    private lateinit var errorInternetPlaceholder: View
    private lateinit var retryButton: Button
    private lateinit var queryInputSearchMusic: EditText
    private lateinit var clearButton: ImageButton
    private var lastQuery: String = ""

    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        queryInputSearchMusic = findViewById(R.id.inputEditTextSearch)
        queryInputSearchMusic.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = queryInputSearchMusic.text.toString()
                searchSongs(query)
                true
            }
            false
        }

        val trackList = mutableListOf<Track>()

        trackAdapter = TrackAdapter(trackList)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = trackAdapter

        noResultsPlaceholder = findViewById(R.id.no_results_placeholder)
        errorInternetPlaceholder = findViewById(R.id.connection_error_placeholder)
        retryButton = findViewById(R.id.retry_button)

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val inputEditTextSearch = findViewById<AppCompatEditText>(R.id.inputEditTextSearch)
        clearButton = findViewById<ImageButton>(R.id.clear_search_button_icon)

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
            clearSearchResults()
        }

        retryButton.setOnClickListener {
            searchSongs(lastQuery)
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

    private fun searchSongs(query: String) {
        if (query.isNotEmpty()) {
            lastQuery = query
            val call = RetrofitClient.iTunesApiService.searchSongs(query)
            call.enqueue(object : Callback<TrackSearchResponse> {
                override fun onResponse(
                    call: Call<TrackSearchResponse>,
                    response: Response<TrackSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val songs = response.body()?.results ?: emptyList()
                        if (songs.isEmpty()) {
                            showNoResultsPlaceholder()
                        } else {
                            trackAdapter.updateData(songs)
                            showResults()
                        }
                    } else {
                        showErrorPlaceholder()
                    }
                }

                override fun onFailure(call: Call<TrackSearchResponse>, t: Throwable) {
                    showErrorPlaceholder()
                }
            })
        } else {
            clearSearchResults()
        }
    }

    private fun clearSearchResults() {
        trackAdapter.updateData(emptyList())
        recyclerView.visibility = View.GONE
        noResultsPlaceholder.visibility = View.GONE
        errorInternetPlaceholder.visibility = View.GONE
    }

    private fun showResults() {
        recyclerView.visibility = View.VISIBLE
        noResultsPlaceholder.visibility = View.GONE
        errorInternetPlaceholder.visibility = View.GONE
    }

    private fun showNoResultsPlaceholder() {
        recyclerView.visibility = View.GONE
        noResultsPlaceholder.visibility = View.VISIBLE
        errorInternetPlaceholder.visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        recyclerView.visibility = View.GONE
        noResultsPlaceholder.visibility = View.GONE
        errorInternetPlaceholder.visibility = View.VISIBLE
    }

}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

