package com.stormbeats.app.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.stormbeats.app.BuildConfig
import com.stormbeats.app.data.model.GitHubRelease
import com.stormbeats.app.data.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object UpdateManager {

    // CHANGE THESE to your GitHub username and repo name
    const val GITHUB_OWNER = "spritzerXd976"
    const val GITHUB_REPO = "STROMBEATS"

    private val repository = MusicRepository()

    suspend fun checkForUpdate(context: Context): UpdateResult = withContext(Dispatchers.IO) {
        try {
            val result = repository.getLatestRelease(GITHUB_OWNER, GITHUB_REPO)
            if (result.isSuccess) {
                val release = result.getOrNull()!!
                val latestVersion = release.tagName.removePrefix("v")
                val currentVersion = BuildConfig.VERSION_NAME

                if (isNewerVersion(latestVersion, currentVersion)) {
                    val apkAsset = release.assets.find { it.name.endsWith(".apk") }
                    UpdateResult.UpdateAvailable(release, apkAsset?.downloadUrl ?: "")
                } else {
                    UpdateResult.UpToDate
                }
            } else {
                UpdateResult.Error("Could not check for updates")
            }
        } catch (e: Exception) {
            UpdateResult.Error(e.message ?: "Unknown error")
        }
    }

    fun downloadAndInstall(context: Context, downloadUrl: String, versionName: String) {
        val fileName = "StormBeats-$versionName.apk"
        val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
            setTitle("StormBeats Update")
            setDescription("Downloading version $versionName...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setMimeType("application/vnd.android.package-archive")
        }
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
    }

    fun installApk(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        return try {
            val latestParts = latest.split(".").map { it.toInt() }
            val currentParts = current.split(".").map { it.toInt() }
            for (i in 0..2) {
                val l = latestParts.getOrElse(i) { 0 }
                val c = currentParts.getOrElse(i) { 0 }
                if (l > c) return true
                if (l < c) return false
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    sealed class UpdateResult {
        object UpToDate : UpdateResult()
        data class UpdateAvailable(val release: GitHubRelease, val downloadUrl: String) : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }
}
