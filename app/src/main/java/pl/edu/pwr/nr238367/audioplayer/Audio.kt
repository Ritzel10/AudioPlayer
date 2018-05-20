package pl.edu.pwr.nr238367.audioplayer

import java.text.DecimalFormat

const val SECONDS_IN_MINUTE = 60

data class Audio(val title: String, val author: String, val durationSeconds: Long, val audioName: String) {
    val durationToString:String
        get() {
            val minutes = durationSeconds / SECONDS_IN_MINUTE
            val seconds = durationSeconds - SECONDS_IN_MINUTE * minutes
            val df = DecimalFormat("00")
            return "${df.format(minutes)}:${df.format(seconds)}"
        }
}