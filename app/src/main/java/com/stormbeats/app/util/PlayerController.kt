package com.stormbeats.app.util

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.stormbeats.app.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PlayerController {

    private var player: ExoPlayer? = null
    private var _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private var _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private var queue: List<Song> = emptyList()
    private var currentIndex = 0

    fun init(context: Context) {
        if (player == null) {
            player = ExoPlayer.Builder(context.applicationContext).build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }
                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        _currentSong.value = queue.getOrNull(currentIndex)
                    }
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            playNext()
                        }
                    }
                })
            }
        }
    }

    fun playSong(song: Song, songList: List<Song> = listOf(song)) {
        queue = songList
        currentIndex = songList.indexOf(song).coerceAtLeast(0)
        _currentSong.value = song
        val streamUrl = song.getStreamUrl()
        if (streamUrl.isEmpty()) return
        player?.apply {
            setMediaItem(MediaItem.fromUri(streamUrl))
            prepare()
            play()
        }
    }

    fun playNext() {
        if (currentIndex < queue.size - 1) {
            currentIndex++
            playSong(queue[currentIndex], queue)
        }
    }

    fun playPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            playSong(queue[currentIndex], queue)
        }
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun seekTo(position: Long) {
        player?.seekTo(position)
    }

    fun getCurrentPosition(): Long = player?.currentPosition ?: 0L

    fun getDuration(): Long = player?.duration?.coerceAtLeast(0L) ?: 0L

    fun release() {
        player?.release()
        player = null
    }
}
