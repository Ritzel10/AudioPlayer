package pl.edu.pwr.nr238367.audioplayer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.DecimalFormat

const val SECONDS_IN_MINUTE = 60

@Parcelize
data class Audio(val title: String, val author: String, val durationSeconds: Int, val audioPath: String = "") : Parcelable {
    val durationToString:String
        get() {
            val minutes = durationSeconds / SECONDS_IN_MINUTE
            val seconds = durationSeconds - SECONDS_IN_MINUTE * minutes
            val df = DecimalFormat("00")
            return "${df.format(minutes)}:${df.format(seconds)}"
        }
}