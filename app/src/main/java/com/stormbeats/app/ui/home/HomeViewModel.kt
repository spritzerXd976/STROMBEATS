package com.stormbeats.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stormbeats.app.data.model.ArtistResult
import com.stormbeats.app.data.model.Song
import com.stormbeats.app.data.repository.MusicRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repo = MusicRepository()

    private val _trending   = MutableStateFlow<List<Song>>(emptyList())
    val trending: StateFlow<List<Song>> = _trending

    private val _newReleases = MutableStateFlow<List<Song>>(emptyList())
    val newReleases: StateFlow<List<Song>> = _newReleases

    private val _recentSongs = MutableStateFlow<List<Song>>(emptyList())
    val recentSongs: StateFlow<List<Song>> = _recentSongs

    private val _artists     = MutableStateFlow<List<ArtistResult>>(emptyList())
    val artists: StateFlow<List<ArtistResult>> = _artists

    private val _isLoading   = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true

            val t = async { repo.searchSongs("top hindi hits 2024",   limit = 10) }
            val n = async { repo.searchSongs("new bollywood 2024",    limit = 10) }
            val r = async { repo.searchSongs("bollywood trending",    limit = 8)  }
            val a = async { repo.searchArtists("arijit pritam rahman neha", limit = 8) }

            t.await().onSuccess { _trending.value    = it }
            n.await().onSuccess { _newReleases.value = it }
            r.await().onSuccess { _recentSongs.value = it }
            a.await().onSuccess { _artists.value     = it }

            _isLoading.value = false
        }
    }

    fun addToRecent(song: Song) {
        val list = _recentSongs.value.toMutableList()
        list.removeAll { it.id == song.id }
        list.add(0, song)
        _recentSongs.value = list.take(12)
    }
}
