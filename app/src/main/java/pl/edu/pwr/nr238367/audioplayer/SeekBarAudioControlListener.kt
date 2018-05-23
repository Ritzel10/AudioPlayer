package pl.edu.pwr.nr238367.audioplayer

import android.widget.SeekBar


class SeekBarAudioControlListener(var playbackManager: PlaybackManager? = null) : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            playbackManager?.seekToSeconds(progress)
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

}