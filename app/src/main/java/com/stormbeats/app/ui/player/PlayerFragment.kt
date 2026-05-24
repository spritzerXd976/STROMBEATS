package com.stormbeats.app.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
                    binding.seekBar.max = it.getDurationSeconds().toInt()
                }
            }
        }

        scope.launch {
            PlayerController.isPlaying.collect { playing ->
                binding.btnPlayPause.setImageResource(
                    if (playing) R.drawable.ic_pause else R.drawable.ic_play
                )
            }
        }

        binding.btnPlayPause.setOnClickListener { PlayerController.togglePlayPause() }
        binding.btnNext.setOnClickListener { PlayerController.playNext() }
        binding.btnPrevious.setOnClickListener { PlayerController.playPrevious() }

        binding.seekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) PlayerController.seekTo(progress * 1000L)
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        startSeekBarUpdate()
    }

    private fun startSeekBarUpdate() {
        seekRunnable = object : Runnable {
            override fun run() {
                val pos = PlayerController.getCurrentPosition()
                binding.seekBar.progress = (pos / 1000).toInt()
                binding.currentTime.text = formatTime(pos)
                binding.totalTime.text = formatTime(PlayerController.getDuration())
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
