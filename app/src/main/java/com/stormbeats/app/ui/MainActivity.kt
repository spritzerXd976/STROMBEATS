package com.stormbeats.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.stormbeats.app.service.MusicService
import com.stormbeats.app.ui.compose.MainScreen
import com.stormbeats.app.ui.theme.StormBeatsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Start the media service so notification controls work
        startService(Intent(this, MusicService::class.java))

        setContent {
            StormBeatsTheme {
                MainScreen()
            }
        }
    }
}
