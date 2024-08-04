package com.example.playlistmaker.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.playlistmaker.Creator
import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.interactor.TrackInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.ui.AudioPlayer.Companion.DATA_TRACK


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var trackInteractor: TrackInteractor

    private var searchText: String = ""

    private lateinit var handler: Handler
    private var searchRunnable: Runnable? = null

    private var isHistoryLoaded = false

    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trackInteractor = Creator.provideTrackInteractor(this)

        handler = Handler(Looper.getMainLooper())
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            handleTrackClick(track)
        }
        searchHistoryAdapter = TrackAdapter(mutableListOf()) { track ->
            val trackDto = TrackCreator.map(track)
            val intent = Intent(this, AudioPlayer::class.java).apply {
                putExtra(DATA_TRACK, trackDto)
            }
            startActivity(intent)
        }

        binding.searchRecyclerViewItunes.adapter = trackAdapter
        binding.searchHistoryRecyclerView.adapter = searchHistoryAdapter

        binding.inputEditTextSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.searchHint.visibility =
                if (hasFocus && binding.inputEditTextSearch.text!!.isEmpty()) View.VISIBLE else View.GONE
            if (hasFocus && searchText.isEmpty()) {
                loadSearchHistory()
            }
        }

        binding.inputEditTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.inputEditTextSearch.text.toString()
                searchSongs(query)
                true
            } else {
                false
            }
        }

        binding.clearSearchButtonIcon.setOnClickListener {
            binding.inputEditTextSearch.setText("")
            binding.inputEditTextSearch.hideKeyboard()
            binding.inputEditTextSearch.clearFocus()
            clearSearchResults()
        }

        binding.retryButton.setOnClickListener {
            searchSongs(searchText)
        }

        binding.clearHistoryButton.setOnClickListener {
            clearSearchHistory()
        }

        binding.inputEditTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
                if (searchText.isEmpty()) {
                    if (!isHistoryLoaded) {
                        loadSearchHistory()
                    }
                } else {
                    binding.searchHistoryLayout.visibility = View.GONE
                    isHistoryLoaded = false
                }
                binding.clearSearchButtonIcon.isVisible = !s.isNullOrEmpty()

                searchRunnable?.let { handler.removeCallbacks(it) }
                searchRunnable = Runnable { searchSongs(searchText) }
                handler.postDelayed(searchRunnable!!, CLICK_DEBOUNCE_DELAY)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchHint.isVisible =
                    binding.inputEditTextSearch.hasFocus() && s.isNullOrEmpty()
            }
        })

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            binding.inputEditTextSearch.setText(searchText)
        }

        binding.searchToolbar.setNavigationOnClickListener {
            finish()
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
            showProgressBar()
            try {
                trackInteractor.searchSongs(query) { songs ->
                    binding.searchProgressBar.visibility = View.GONE
                    if (songs.isEmpty()) {
                        showNoResultsPlaceholder()
                    } else {
                        trackAdapter.updateData(songs)
                        showResults()
                    }
                }
            } catch (e: Exception) {
                binding.searchProgressBar.visibility = View.GONE
                showErrorPlaceholder()
            }
        } else {
            clearSearchResults()
            loadSearchHistory()
        }
    }

    private fun clearSearchResults() {
        trackAdapter.updateData(emptyList())
        binding.searchHistoryLayout.visibility = View.GONE
        binding.searchRecyclerViewItunes.visibility = View.GONE
        binding.noResultsPlaceholder.visibility = View.GONE
        binding.connectionErrorPlaceholder.visibility = View.GONE
    }

    private fun showResults() {
        binding.searchRecyclerViewItunes.visibility = View.VISIBLE
        binding.noResultsPlaceholder.visibility = View.GONE
        binding.connectionErrorPlaceholder.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.GONE
    }

    private fun showNoResultsPlaceholder() {
        binding.searchRecyclerViewItunes.visibility = View.GONE
        binding.noResultsPlaceholder.visibility = View.VISIBLE
        binding.connectionErrorPlaceholder.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        binding.searchRecyclerViewItunes.visibility = View.GONE
        binding.noResultsPlaceholder.visibility = View.GONE
        binding.connectionErrorPlaceholder.visibility = View.VISIBLE
        binding.searchHistoryLayout.visibility = View.GONE
    }

    private fun loadSearchHistory() {
        val historyList = trackInteractor.getSearchHistory()
        if (historyList.isNotEmpty()) {
            searchHistoryAdapter.updateData(historyList)
            binding.searchHistoryLayout.visibility = View.VISIBLE
        } else {
            binding.searchHistoryLayout.visibility = View.GONE
        }
        trackAdapter.updateData(historyList)
    }

    private fun clearSearchHistory() {
        binding.searchHistoryLayout.visibility = View.GONE
        trackInteractor.clearSearchHistory()
        clearSearchResults()
    }

    private fun showProgressBar() {
        binding.searchProgressBar.visibility = View.VISIBLE
        binding.searchHistoryLayout.visibility = View.GONE
    }

    private fun handleTrackClick(track: Track) {
        val context = this
        val trackDto = TrackCreator.map(track)
        val intent = Intent(context, AudioPlayer::class.java).apply {
            putExtra(DATA_TRACK, trackDto)
        }
        context.startActivity(intent)
        trackInteractor.saveSearchHistory(track)
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}



