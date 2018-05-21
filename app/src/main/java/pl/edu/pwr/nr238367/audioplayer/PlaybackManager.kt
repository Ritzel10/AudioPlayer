package pl.edu.pwr.nr238367.audioplayer

import android.content.Context
import android.media.MediaPlayer

const val DIRECTION_FORWARDS = 1
const val DIRECTION_BACKWARDS = -1
const val DEFAULT_SKIP_AMOUNT = 10000
class PlaybackManager(private val context: Context) {
    var mediaPlayer: MediaPlayer? = null
    var currentlyPlaying: Audio? = null

    fun startAudio(audio: Audio) {
        mediaPlayer?.let {
            mediaPlayer!!.release()
        }
        currentlyPlaying = audio
//        (context as MainActivity).changeSongTitle(audio.title)
        mediaPlayer = MediaPlayer.create(context, context.resources.getIdentifier(audio.audioName, "raw", context.packageName))
        mediaPlayer!!.start()
    }

    val isPlaying: Boolean
        get() {
            return mediaPlayer?.isPlaying ?: false
        }

    val isSongCreated: Boolean
        get() = currentlyPlaying != null

    fun skipBackwards() {
        skip(DEFAULT_SKIP_AMOUNT, DIRECTION_BACKWARDS)
    }

    fun skipForwards() {
        skip(DEFAULT_SKIP_AMOUNT, DIRECTION_FORWARDS)
    }

    fun skip(duration: Int, direction: Int) {
        mediaPlayer?.seekTo(mediaPlayer!!.currentPosition + direction * duration)
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

}