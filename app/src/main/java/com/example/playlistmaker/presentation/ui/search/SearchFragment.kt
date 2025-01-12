package com.example.playlistmaker.presentation.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.data.TrackCreator
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.presentation.ui.AudioPlayer
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private val searchViewModel: SearchViewModel by viewModel()

    private var searchText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter(
            mutableListOf(),
            onItemClick = { track ->
                handleTrackClick(track)
            },
            onItemLongClick = null
        )
        searchHistoryAdapter = TrackAdapter(
            mutableListOf(),
            onItemClick = { track ->
                handleTrackClick(track)
            },
            onItemLongClick = null
        )

        binding.searchRecyclerViewItunes.adapter = trackAdapter
        binding.searchHistoryRecyclerView.adapter = searchHistoryAdapter

        searchViewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                binding.searchRecyclerViewItunes.visibility =
                    if (tracks.isNotEmpty()) View.VISIBLE else View.GONE
                binding.searchHistoryLayout.visibility = View.GONE
                trackAdapter.updateData(tracks)
            } else {
                binding.searchRecyclerViewItunes.visibility =
                    if (tracks.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }


        searchViewModel.searchHistory.observe(viewLifecycleOwner) { history ->
            if (history.isNotEmpty() && binding.inputEditTextSearch.hasFocus()) {
                searchHistoryAdapter.updateData(history)
                binding.searchHistoryLayout.visibility = View.VISIBLE
                binding.searchRecyclerViewItunes.visibility = View.GONE
            }
        }

        searchViewModel.showProgressBar.observe(viewLifecycleOwner) { show ->
            binding.searchProgressBar.visibility = if (show) View.VISIBLE else View.GONE
            binding.searchHistoryLayout.visibility = View.GONE
        }

        searchViewModel.showNoResultsPlaceholder.observe(viewLifecycleOwner) { show ->
            binding.noResultsPlaceholder.visibility = if (show) View.VISIBLE else View.GONE
        }

        searchViewModel.showErrorPlaceholder.observe(viewLifecycleOwner) { show ->
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
            }
        })



        binding.inputEditTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchText.isEmpty()) {
                searchViewModel.loadSearchHistory()
            } else {
                clearSearchResults()
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
            searchHistoryAdapter.updateData(emptyList())
            binding.searchHistoryLayout.visibility = View.GONE
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "") ?: ""
            binding.inputEditTextSearch.setText(searchText)
        }

        binding.retryButton.setOnClickListener {
            searchViewModel.retrySearch()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    private fun clearSearchResults() {
        trackAdapter.updateData(emptyList())
        binding.searchHistoryLayout.visibility = View.GONE
        binding.searchRecyclerViewItunes.visibility = View.GONE
        binding.noResultsPlaceholder.visibility = View.GONE
        binding.connectionErrorPlaceholder.visibility = View.GONE
    }

    private fun handleTrackClick(track: Track) {
        val trackDto = TrackCreator.map(track)
        val intent = Intent(requireContext(), AudioPlayer::class.java).apply {
            putExtra(AudioPlayer.DATA_TRACK, trackDto)
        }
        startActivity(intent)
        searchViewModel.saveTrack(track)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        if (binding.inputEditTextSearch.text.isNullOrEmpty()) {
            clearSearchResults()
            searchViewModel.clearTracks()
            searchViewModel.loadSearchHistory()
        }
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}