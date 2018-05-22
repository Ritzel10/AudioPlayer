package pl.edu.pwr.nr238367.audioplayer

import android.media.MediaPlayer
import android.widget.MediaController.MediaPlayerControl


class AudioController(private val mediaPlayer: MediaPlayer?) : MediaPlayerControl {

    override fun getAudioSessionId(): Int {
        return mediaPlayer?.audioSessionId ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun getBufferPercentage(): Int {
        return 0
    }

    override fun seekTo(ms: Int) {
        mediaPlayer?.seekTo(ms)
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun start() {
        mediaPlayer?.start()
    }


    override fun canPause(): Boolean {
        return true
    }

}