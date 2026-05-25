package com.stormbeats.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.stormbeats.app.ui.compose.MainScreen
import com.stormbeats.app.ui.theme.StormBeatsTheme
import com.stormbeats.app.util.PlayerController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        PlayerController.init(this)

        setContent {
            StormBeatsTheme {
                MainScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PlayerController.release()
    }
}
