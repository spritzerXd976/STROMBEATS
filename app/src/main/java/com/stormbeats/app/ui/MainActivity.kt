package com.stormbeats.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.stormbeats.app.ui.compose.MainScreen
import com.stormbeats.app.ui.theme.StormBeatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StormBeatsTheme(darkTheme = true, dynamicColor = true) {
                MainScreen()
            }
        }
    }
}
