package com.stormbeats.app.util

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.stormbeats.app.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PlayerController {

    private var player: ExoPlayer? = null

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private var queue: List<Song> = emptyList()
    private var currentIndex = 0

    fun init(context: Context) {
        if (player != null) return

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        player = ExoPlayer.Builder(context.applicationContext)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        _currentSong.value = queue.getOrNull(currentIndex)
                        _isPlaying.value = player?.isPlaying ?: false
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            playNext()
                        }
                    }
                })
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

    fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
    }

    /** Returns current position in milliseconds */
    fun getCurrentPosition(): Long = player?.currentPosition ?: 0L

    /** Returns total duration in milliseconds */
    fun getDuration(): Long = player?.duration?.coerceAtLeast(0L) ?: 0L

    fun getPlayer(): ExoPlayer? = player

    fun getQueue(): List<Song> = queue.toList()

    fun release() {
        player?.release()
        player = null
    }
}
// Extension already in object — adding getQueue helper at end of file is not needed,
// just verify the method exists
