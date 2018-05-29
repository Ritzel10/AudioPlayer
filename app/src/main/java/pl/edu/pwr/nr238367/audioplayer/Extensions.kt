package pl.edu.pwr.nr238367.audioplayer

import android.media.MediaPlayer
import android.net.Uri

const val DOCUMENT_PATH = "/tree/primary:"
const val REGULAR_PATH = "/storage/emulated/0/"

fun MediaPlayer.currentPositionInSeconds(): Int {
    return this.currentPosition / MILLISECONDS_IN_SECOND
}

fun Uri.convertDocumentPath(): String {
    return this.path.replace(DOCUMENT_PATH, REGULAR_PATH)
}