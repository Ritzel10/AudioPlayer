package pl.edu.pwr.nr238367.audioplayer

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.support.v4.content.LocalBroadcastManager
import pl.edu.pwr.nr238367.audioplayer.TransitionType.*
import java.util.*

const val DIRECTION_FORWARDS = 1
const val DIRECTION_BACKWARDS = -1
const val DEFAULT_SKIP_AMOUNT = 10000
const val MILLISECONDS_IN_SECOND = 1000

class PlaybackManager(private val context: Context, private val audioService: AudioForegroundService) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentPosition: Int = 0
    var transitionType: TransitionType = NORMAL
    var currentlyPlaying: Audio? = null
        private set

    private val onCompletionListener = MediaPlayer.OnCompletionListener {
        nextSong()
    }

    //plays next song, behavior depends on transitionType
    fun nextSong() {
        val intent = Intent(INTENT_FILTER_ACTIVITY_COMMUNICATION)
        if (AudioFolder.audioList.isNotEmpty()) {
            var position = 0
            when (transitionType) {
                REPEAT -> {
                    position = currentPosition
                }
                NORMAL -> {
                    position = (currentPosition + 1) % AudioFolder.audioList.size
                }
                RANDOM -> {
                    var random: Int
                    do {
                        random = Random().nextInt(AudioFolder.audioList.size)
                    } while (random == currentPosition)
                    position = random
                }
            }
            startAudio(position)
            currentPosition = position
            currentlyPlaying = AudioFolder.audioList[position]
            intent.putExtra(MESSAGE_NAME, NowPlaying(currentlyPlaying!!))
            //send information to activity
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }

    fun startAudio(position: Int) {
        mediaPlayer?.let {
            mediaPlayer!!.release()
        }
        val audio = AudioFolder.audioList[position]
        currentlyPlaying = audio
        currentPosition = position
//        mediaPlayer = MediaPlayer.create(context, context.resources.getIdentifier(audio.audioPath, "raw", context.packageName))
        mediaPlayer = MediaPlayer.create(context, Uri.parse(audio.audioPath))
        mediaPlayer?.setOnCompletionListener(onCompletionListener)
        mediaPlayer!!.start()
        audioService.updateNotification(audio)
    }

    val isStarted: Boolean
        get() = mediaPlayer != null
    val isPlaying: Boolean
        get() {
            return mediaPlayer?.isPlaying == true
        }

    fun skipBackwards() {
        skip(DEFAULT_SKIP_AMOUNT, DIRECTION_BACKWARDS)
    }

    val currentTime: Int
        get() {
            return mediaPlayer?.currentPositionInSeconds() ?: 0
        }
    fun skipForwards() {
        skip(DEFAULT_SKIP_AMOUNT, DIRECTION_FORWARDS)
    }

    fun seekToSeconds(duration: Int) {
        mediaPlayer?.seekTo(duration * MILLISECONDS_IN_SECOND)
    }

    private fun skip(duration: Int, direction: Int) {
        mediaPlayer?.seekTo(mediaPlayer!!.currentPosition + direction * duration)
    }

    fun resume() {
        mediaPlayer?.start()
        audioService.updateNotification(currentlyPlaying!!)
    }

    fun pause() {
        mediaPlayer?.pause()
    }
}