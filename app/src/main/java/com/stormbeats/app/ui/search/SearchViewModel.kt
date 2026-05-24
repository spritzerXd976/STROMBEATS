package com.stormbeats.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.data.repository.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val repository = MusicRepository()

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var searchJob: Job? = null

    fun search(query: String) {
        if (query.isBlank()) {
            _songs.value = emptyList()
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // debounce
            _isLoading.value = true
            _error.value = null
            val result = repository.searchSongs(query)
            result.onSuccess { _songs.value = it }
            result.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}
