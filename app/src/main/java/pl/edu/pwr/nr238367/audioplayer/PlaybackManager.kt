package pl.edu.pwr.nr238367.audioplayer

import android.content.Context
import android.media.MediaPlayer

class PlaybackManager(private val context: Context) {
    var mediaPlayer: MediaPlayer? = null
    var currentlyPlaying: Audio? = null

    fun startAudio(audio: Audio) {
        mediaPlayer?.let {
            mediaPlayer!!.release()
        }
        currentlyPlaying = audio
        mediaPlayer = MediaPlayer.create(context, context.resources.getIdentifier(audio.audioName, "raw", context.packageName))
        mediaPlayer!!.start()
    }

    fun skipBackwards() {}
    fun skipForwards() {}
    fun skip(duration: Long, direction: Int) {
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

}