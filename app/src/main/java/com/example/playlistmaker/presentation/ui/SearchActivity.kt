package com.example.playlistmaker.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.ui.AudioPlayer.Companion.DATA_TRACK
import com.example.playlistmaker.presentation.ui.search.SearchViewModel


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var searchViewModel: SearchViewModel

    private var searchText: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchViewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory(this)
        ).get(SearchViewModel::class.java)

        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            handleTrackClick(track)
        }
        searchHistoryAdapter = TrackAdapter(mutableListOf()) { track ->
            handleTrackClick(track)
        }

        binding.searchRecyclerViewItunes.adapter = trackAdapter
        binding.searchHistoryRecyclerView.adapter = searchHistoryAdapter


        searchViewModel.tracks.observe(this) { tracks ->
            if (tracks.isNotEmpty()) {
                trackAdapter.updateData(tracks)
                binding.searchRecyclerViewItunes.isVisible = true
                binding.searchHistoryLayout.isVisible = false
            }
        }


        searchViewModel.searchHistory.observe(this) { history ->
            if (history.isNotEmpty()) {
                searchHistoryAdapter.updateData(history)
                binding.searchHistoryLayout.visibility = View.VISIBLE
                binding.searchRecyclerViewItunes.visibility = View.GONE
            }
        }

        searchViewModel.showProgressBar.observe(this) { show ->
            binding.searchProgressBar.visibility = if (show) View.VISIBLE else View.GONE
            binding.searchHistoryLayout.visibility = View.GONE
        }

        searchViewModel.showNoResultsPlaceholder.observe(this) { show ->
            binding.noResultsPlaceholder.visibility = if (show) View.VISIBLE else View.GONE
        }

        searchViewModel.showErrorPlaceholder.observe(this) { show ->
            binding.connectionErrorPlaceholder.visibility = if (show) View.VISIBLE else View.GONE
        }

        binding.inputEditTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
                searchViewModel.onSearchTextChanged(searchText)
                binding.clearSearchButtonIcon.isVisible = !s.isNullOrEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchHint.isVisible =
                    binding.inputEditTextSearch.hasFocus() && s.isNullOrEmpty()
            }
        })



        binding.inputEditTextSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.searchHint.visibility =
                if (hasFocus && binding.inputEditTextSearch.text.isNullOrEmpty()) View.VISIBLE else View.GONE
            if (hasFocus && searchText.isEmpty()) {
                searchViewModel.loadSearchHistory()
            }
        }

        binding.inputEditTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.inputEditTextSearch.text.toString()
                searchViewModel.onSearchTextChanged(query)
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

        binding.clearHistoryButton.setOnClickListener {
            searchViewModel.clearSearchHistory()
        }

        binding.searchToolbar.setNavigationOnClickListener {
            finish()
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "") ?: ""
            binding.inputEditTextSearch.setText(searchText)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "") ?: ""
    }

    private fun clearSearchResults() {
        trackAdapter.updateData(emptyList())
        binding.searchHistoryLayout.visibility = View.GONE
        binding.searchRecyclerViewItunes.visibility = View.GONE
        binding.noResultsPlaceholder.visibility = View.GONE
        binding.connectionErrorPlaceholder.visibility = View.GONE
    }

    private fun handleTrackClick(track: Track) {
        val context = this
        val trackDto = TrackCreator.map(track)
        val intent = Intent(context, AudioPlayer::class.java).apply {
            putExtra(DATA_TRACK, trackDto)
        }
        context.startActivity(intent)
        searchViewModel.saveTrack(track)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}