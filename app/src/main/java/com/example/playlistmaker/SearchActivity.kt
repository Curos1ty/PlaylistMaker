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
import android.widget.LinearLayout
import android.widget.TextView

import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

import com.example.playlistmaker.model.TrackSearchResponse
import com.example.playlistmaker.network.RetrofitClient

import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response


class SearchActivity : AppCompatActivity() {
    private var searchText: String = ""
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var recyclerViewSearch: RecyclerView
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var noResultsPlaceholder: View
    private lateinit var errorInternetPlaceholder: View
    private lateinit var retryButton: Button
    private lateinit var clearButton: ImageButton
    private lateinit var searchField: EditText
    private lateinit var hintMessage: TextView

    private var lastQuery: String = ""

    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryTitle: TextView
    private lateinit var clearHistoryButton: Button

    private lateinit var visibilityLinerLayoutHistory: LinearLayout

    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        visibilityLinerLayoutHistory = findViewById(R.id.search_history_layout)

        searchField = findViewById(R.id.inputEditTextSearch)
        hintMessage = findViewById(R.id.searchHint)

        clearHistoryButton = findViewById(R.id.clear_history_button)

        searchField.setOnFocusChangeListener { _, hasFocus ->
            hintMessage.visibility =
                if (hasFocus && searchField.text.isEmpty()) View.VISIBLE else View.GONE
        }

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchField.text.toString()
                searchSongs(query)
                true
            }
            false
        }

        recyclerViewSearch = findViewById(R.id.search_recycler_view_itunes)
        recyclerViewHistory = findViewById(R.id.search_history_recycler_view)

        searchHistory = SearchHistory(this)
        searchHistoryTitle = findViewById(R.id.search_history_title)

        trackAdapter = TrackAdapter(mutableListOf(), searchHistory)
        searchHistoryAdapter = TrackAdapter(searchHistory.getHistory(), searchHistory, true)

        recyclerViewSearch.adapter = trackAdapter
        recyclerViewHistory.adapter = searchHistoryAdapter

        loadSearchHistory()

        noResultsPlaceholder = findViewById(R.id.no_results_placeholder)
        errorInternetPlaceholder = findViewById(R.id.connection_error_placeholder)
        retryButton = findViewById(R.id.retry_button)

        val toolbar = findViewById<Toolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearButton = findViewById(R.id.clear_search_button_icon)

        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
                clearButton.isVisible = !s.isNullOrEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hintMessage.isVisible = searchField.hasFocus() && s?.isEmpty() == true
            }
        })

        clearButton.setOnClickListener {
            searchField.setText("")
            searchField.hideKeyboard()
            searchField.clearFocus()
            clearSearchResults()
            loadSearchHistory()
        }

        retryButton.setOnClickListener {
            searchSongs(lastQuery)
        }

        clearHistoryButton.setOnClickListener {
            clearSearchHistory()
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            searchField.setText(searchText)
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
            //Log.i("Запрос для песен не пустой", query)
            lastQuery = query
            val call = RetrofitClient.iTunesApiService.searchSongs(query)

            call.enqueue(object : Callback<TrackSearchResponse> {
                override fun onResponse(
                    call: Call<TrackSearchResponse>, response: Response<TrackSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val songs = response.body()?.results ?: emptyList()
                        //Log.i("Проверка Response", songs.toString())
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
        recyclerViewSearch.visibility = View.GONE
        noResultsPlaceholder.visibility = View.GONE
        errorInternetPlaceholder.visibility = View.GONE
    }

    private fun showResults() {
        //Log.i("Сюда зашло", recyclerViewSearch.visibility.toString())
        recyclerViewSearch.visibility = View.VISIBLE
        noResultsPlaceholder.visibility = View.GONE
        errorInternetPlaceholder.visibility = View.GONE
        visibilityLinerLayoutHistory.visibility = View.GONE
    }

    private fun showNoResultsPlaceholder() {
        recyclerViewSearch.visibility = View.GONE
        noResultsPlaceholder.visibility = View.VISIBLE
        errorInternetPlaceholder.visibility = View.GONE
        visibilityLinerLayoutHistory.visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        recyclerViewSearch.visibility = View.GONE
        noResultsPlaceholder.visibility = View.GONE
        errorInternetPlaceholder.visibility = View.VISIBLE
        visibilityLinerLayoutHistory.visibility = View.GONE
    }

    private fun loadSearchHistory() {
        val historyList = searchHistory.getHistory()
        if (historyList.isNotEmpty()) {
            searchHistoryAdapter.updateData(historyList)
            visibilityLinerLayoutHistory.visibility = View.VISIBLE
        } else {
            visibilityLinerLayoutHistory.visibility = View.GONE
        }
        trackAdapter.updateData(historyList)
    }

    private fun clearSearchHistory() {
        visibilityLinerLayoutHistory.visibility = View.GONE
        searchHistory.clearHistory()
        clearSearchResults()
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}



