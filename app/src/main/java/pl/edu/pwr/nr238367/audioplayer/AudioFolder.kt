package pl.edu.pwr.nr238367.audioplayer


import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import java.io.File

object AudioFolder {
    var audioList: MutableList<Audio> = mutableListOf()
    private val supportedExtensions = listOf("3gp", "m4a", "aac", "flac", "mp3", "wav", "ogg")
    fun init(path: String, context: Context) {
        audioList.clear()
        try {
            val directory = File(path)
            for (file in directory.listFiles()) {
                //if file is an audio file
                if (file.extension in supportedExtensions) {
                    val audio = extractMetaDataFromFile(file, context)
                    audio?.let {
                        audioList.add(audio)
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("file", e.toString())
        }
    }

    private fun extractMetaDataFromFile(file: File, context: Context): Audio? {
        val dataRetriever = MediaMetadataRetriever()
        dataRetriever.setDataSource(file.absolutePath)
        return try {
            val title = dataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    ?: file.nameWithoutExtension
            val artist = dataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                    ?: context.getString(R.string.no_artist)
            val duration = dataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            Audio(title, artist, duration.toInt() / MILLISECONDS_IN_SECOND, file.absolutePath)
        } catch (e: Exception) {
            Log.d("", e.toString())
            null
        }

    }
}