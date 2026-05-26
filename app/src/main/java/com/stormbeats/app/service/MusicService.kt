package com.stormbeats.app.service

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.stormbeats.app.ui.MainActivity
import com.stormbeats.app.util.PlayerController

class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        // Re-use the same ExoPlayer instance that PlayerController manages.
        // This ensures UI controls and the notification are in sync.
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
