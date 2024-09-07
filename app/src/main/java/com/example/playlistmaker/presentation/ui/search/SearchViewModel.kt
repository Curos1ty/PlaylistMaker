package com.example.playlistmaker.presentation.ui.search

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    private val _searchHistory = MutableLiveData<List<Track>>()
    val searchHistory: LiveData<List<Track>> = _searchHistory

    private val _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    private val _showNoResultsPlaceholder = MutableLiveData<Boolean>()
    val showNoResultsPlaceholder: LiveData<Boolean> = _showNoResultsPlaceholder

    private val _showErrorPlaceholder = MutableLiveData<Boolean>()
    val showErrorPlaceholder: LiveData<Boolean> = _showErrorPlaceholder

    private var isSearching = false

    private var searchRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    private val debounceDelay = 2000L


    private fun searchSongs(query: String) {
        _showProgressBar.value = true
        isSearching = true
        viewModelScope.launch {
            try {
                val songs = searchInteractor.searchSongs(query)

                _showProgressBar.value = false
                if (songs.isEmpty()) {
                    _showNoResultsPlaceholder.value = true
                } else {
                    _tracks.value = songs
                    _searchHistory.value = emptyList()
                }
            } catch (e: Exception) {
                _showProgressBar.value = false
                _showErrorPlaceholder.value = true
            } finally {
                isSearching = false
            }
        }
    }

    fun onSearchTextChanged(query: String) {
        searchRunnable?.let { handler.removeCallbacks(it) }
        if (query.isEmpty()) {
            loadSearchHistory()
            return
        } else {
            _showNoResultsPlaceholder.value = false
            _showErrorPlaceholder.value = false

            searchRunnable = Runnable { searchSongs(query) }
            handler.postDelayed(searchRunnable!!, debounceDelay)
        }

    }

    fun loadSearchHistory() {
        if (!isSearching) {
            val history = searchInteractor.getSearchHistory()
            _searchHistory.value = history
        }
    }

    fun clearSearchHistory() {
        searchInteractor.clearSearchHistory()
        _searchHistory.value = emptyList()
    }

    fun saveTrack(track: Track) {
        searchInteractor.saveSearchHistory(track)
    }
}