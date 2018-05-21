package pl.edu.pwr.nr238367.audioplayer

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder


class AudioService : Service() {
    val audioList = listOf(Audio("Energy", "Bensound", 179, "bensoundenergy"))
    lateinit var playbackManager: PlaybackManager
    override fun onBind(intent: Intent): IBinder? {
        playbackManager = PlaybackManager(this)
        return ServiceBinder()
    }

    inner class ServiceBinder : Binder() {
        fun getService(): AudioService {
            return this@AudioService
        }

        val playbackManager: PlaybackManager get() = this@AudioService.playbackManager
    }
}