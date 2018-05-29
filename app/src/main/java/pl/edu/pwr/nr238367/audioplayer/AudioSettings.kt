package pl.edu.pwr.nr238367.audioplayer

import android.content.Context
import android.preference.PreferenceManager


class AudioSettings(private val context: Context, private val playbackManager: PlaybackManager) {
    fun loadSettings() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val position = sharedPreferences.getString(context.getString(R.string.transition_key), DEFAULT_TRANSITION_SPINNER_POSITION.toString())
        changeServiceTransitionType(Integer.valueOf(position))
    }

    fun changeServiceTransitionType(position: Int) {
        playbackManager.transitionType = TransitionType.values()[position]
    }
}