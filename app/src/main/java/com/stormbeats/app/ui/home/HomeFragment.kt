package com.stormbeats.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stormbeats.app.databinding.FragmentHomeBinding
import com.stormbeats.app.util.UpdateManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkForUpdates()
    }

    private fun checkForUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = UpdateManager.checkForUpdate(requireContext())
            if (result is UpdateManager.UpdateResult.UpdateAvailable) {
                showUpdateDialog(result)
            }
        }
    }

    private fun showUpdateDialog(update: UpdateManager.UpdateResult.UpdateAvailable) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🎵 New Update Available!")
            .setMessage("Version ${update.release.tagName} is ready.\n\n${update.release.body}")
            .setPositiveButton("Update Now") { _, _ ->
                UpdateManager.downloadAndInstall(
                    requireContext(),
                    update.downloadUrl,
                    update.release.tagName
                )
            }
            .setNegativeButton("Later", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
