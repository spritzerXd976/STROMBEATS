package com.stormbeats.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.stormbeats.app.ui.compose.MainScreen
import com.stormbeats.app.ui.theme.StormBeatsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // PlayerController already initialized in StormBeatsApp; no double-init needed
        setContent {
            StormBeatsTheme {
                MainScreen()
            }
        }
    }
}
