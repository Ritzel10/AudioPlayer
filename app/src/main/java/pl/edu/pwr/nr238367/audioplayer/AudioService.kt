package pl.edu.pwr.nr238367.audioplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder


class AudioService : Service() {
    val audioList = listOf(Audio("Energy", "Bensound", 179, "bensoundenergy"))
    //    lateinit var playbackManager: PlaybackManager
    var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder? {
//        playbackManager = PlaybackManager(this)
        mediaPlayer = MediaPlayer.create(this, this.resources.getIdentifier(audioList[0].audioName, "raw", packageName))
        return ServiceBinder()
    }

    inner class ServiceBinder : Binder() {
        fun getService(): AudioService {
            return this@AudioService
        }

        val mediaPlayer: MediaPlayer? get() = this@AudioService.mediaPlayer
//        val playbackManager: PlaybackManager get() = this@AudioService.playbackManager
    }
}