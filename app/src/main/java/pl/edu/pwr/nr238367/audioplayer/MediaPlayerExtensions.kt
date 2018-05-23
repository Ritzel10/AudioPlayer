package pl.edu.pwr.nr238367.audioplayer

import android.media.MediaPlayer


fun MediaPlayer.currentPositionInSeconds(): Int {
    return this.currentPosition / MILLISECONDS_IN_SECOND
}