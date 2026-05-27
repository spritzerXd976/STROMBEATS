package com.stormbeats.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.stormbeats.app.R
import com.stormbeats.app.ui.MainActivity
import com.stormbeats.app.util.PlayerController

class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "stormbeats_playback"
        const val NOTIFICATION_CHANNEL_NAME = "StormBeats Playback"
    }

    override fun onCreate() {
        super.onCreate()

        // Create a styled notification channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Music playback controls"
                lightColor  = Color.parseColor("#7C3AED")   // violet accent
                enableLights(true)
                setShowBadge(false)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm?.createNotificationChannel(channel)
        }

        // Re-use the same ExoPlayer instance that PlayerController manages.
        PlayerController.init(applicationContext)
        val player = PlayerController.getPlayer() ?: return

        val sessionActivityIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            // Do NOT release the player here — PlayerController owns it.
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
