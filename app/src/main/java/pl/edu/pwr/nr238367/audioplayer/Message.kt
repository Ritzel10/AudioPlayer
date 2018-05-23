package pl.edu.pwr.nr238367.audioplayer

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


sealed class Message : Parcelable
@Parcelize
data class AudioFinished(val finished: Audio) : Message(), Parcelable

@Parcelize
data class NowPlaying(val playing: Audio) : Message(), Parcelable