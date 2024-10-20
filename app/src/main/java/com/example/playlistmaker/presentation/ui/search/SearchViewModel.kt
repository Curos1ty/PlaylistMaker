package com.example.playlistmaker.presentation.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.SearchInteractor
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
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

    private val searchQueryFlow = MutableStateFlow("")

    init {
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(2000)
                .filter { query -> query.isNotEmpty() }
                .distinctUntilChanged()
                .collectLatest { query ->
                    searchSongs(query)
                }
        }
    }

    private suspend fun searchSongs(query: String) {
        _showProgressBar.value = true
        searchInteractor.searchSongs(query).collectLatest { songs ->
            _showProgressBar.value = false
            if (songs.isEmpty()) {
                _showNoResultsPlaceholder.value = true
            } else {
                _tracks.value = songs
                _searchHistory.value = emptyList()
            }
        }
    }

    fun onSearchTextChanged(query: String) {
        _showNoResultsPlaceholder.value = false
        _showErrorPlaceholder.value = false
        searchQueryFlow.value = query

        if (query.isEmpty()) {
            loadSearchHistory()
        }
    }

    fun loadSearchHistory() {
        if (searchQueryFlow.value.isEmpty()) {
            val history = searchInteractor.getSearchHistory()
            _searchHistory.value = history
        }
    }

    fun clearSearchHistory() {
        searchInteractor.clearSearchHistory()
        _searchHistory.value = emptyList()
    }

    fun clearTracks() {
        _tracks.value = emptyList()
    }

    fun saveTrack(track: Track) {
        searchInteractor.saveSearchHistory(track)
    }
}