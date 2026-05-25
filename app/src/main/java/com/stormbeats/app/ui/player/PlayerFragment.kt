package com.stormbeats.app.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider
import com.stormbeats.app.R
import com.stormbeats.app.databinding.FragmentPlayerBinding
import com.stormbeats.app.util.PlayerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PlayerFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val scope = CoroutineScope(Dispatchers.Main)
    private val handler = Handler(Looper.getMainLooper())
    private var seekRunnable: Runnable? = null
    private var isUserSeeking = false

    override fun getTheme() = R.style.Theme_StormBeats_BottomSheet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scope.launch {
            PlayerController.currentSong.collect { song ->
                song?.let {
                    binding.songTitle.text = it.name
                    binding.artistName.text = it.getPrimaryArtist()
                    binding.albumName.text = it.album?.name ?: ""
                    Glide.with(this@PlayerFragment)
                        .load(it.getImageUrl())
                        .placeholder(R.drawable.ic_music_note)
                        .centerCrop()
                        .into(binding.albumArt)
                    val durSecs = it.getDurationSeconds().toFloat()
                    if (durSecs > 0) {
                        binding.seekBar.valueTo = durSecs
                    }
                }
            }
        }

        scope.launch {
            PlayerController.isPlaying.collect { playing ->
                binding.btnPlayPause.setIconResource(
                    if (playing) R.drawable.ic_pause else R.drawable.ic_play
                )
            }
        }

        binding.btnPlayPause.setOnClickListener { PlayerController.togglePlayPause() }
        binding.btnNext.setOnClickListener { PlayerController.playNext() }
        binding.btnPrevious.setOnClickListener { PlayerController.playPrevious() }

        binding.seekBar.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) { isUserSeeking = true }
            override fun onStopTrackingTouch(slider: Slider) {
                isUserSeeking = false
                PlayerController.seekTo(slider.value.toLong() * 1000L)
            }
        })

        binding.seekBar.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                binding.currentTime.text = formatTime(value.toLong() * 1000L)
            }
        }

        startSeekBarUpdate()
    }

    private fun startSeekBarUpdate() {
        seekRunnable = object : Runnable {
            override fun run() {
                if (!isUserSeeking && _binding != null) {
                    val posMs = PlayerController.getCurrentPosition()
                    val durMs = PlayerController.getDuration()
                    val posSecs = (posMs / 1000).toFloat()
                    val durSecs = (durMs / 1000).toFloat()
                    if (durSecs > 0 && binding.seekBar.valueTo != durSecs) {
                        binding.seekBar.valueTo = durSecs
                    }
                    if (durSecs > 0 && posSecs <= durSecs) {
                        binding.seekBar.value = posSecs
                    }
                    binding.currentTime.text = formatTime(posMs)
                    binding.totalTime.text = formatTime(durMs)
                }
                handler.postDelayed(this, 500)
            }
        }
        handler.post(seekRunnable!!)
    }

    private fun formatTime(ms: Long): String {
        val secs = ms / 1000
        return "%d:%02d".format(secs / 60, secs % 60)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        seekRunnable?.let { handler.removeCallbacks(it) }
        scope.cancel()
        _binding = null
    }
}
