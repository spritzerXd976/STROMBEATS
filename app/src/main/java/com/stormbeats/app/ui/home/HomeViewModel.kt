package com.stormbeats.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.data.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = MusicRepository()

    private val _recentSongs = MutableStateFlow<List<Song>>(emptyList())
    val recentSongs: StateFlow<List<Song>> = _recentSongs

    private val _featuredSongs = MutableStateFlow<List<Song>>(emptyList())
    val featuredSongs: StateFlow<List<Song>> = _featuredSongs

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadHomeData() }

    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.searchSongs("top hindi hits 2024", page = 1)
                .onSuccess { _featuredSongs.value = it.take(6) }
            repository.searchSongs("bollywood trending 2024", page = 1)
                .onSuccess { _recentSongs.value = it.take(8) }
            _isLoading.value = false
        }
    }

    fun addToRecent(song: Song) {
        val current = _recentSongs.value.toMutableList()
        current.removeAll { it.id == song.id }
        current.add(0, song)
        _recentSongs.value = current.take(10)
    }
}
