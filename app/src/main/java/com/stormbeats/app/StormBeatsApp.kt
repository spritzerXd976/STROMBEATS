package com.stormbeats.app

import android.app.Application
import com.stormbeats.app.util.PlayerController

class StormBeatsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PlayerController.init(this)
    }
}
