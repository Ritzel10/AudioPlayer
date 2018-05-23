package pl.edu.pwr.nr238367.audioplayer


interface AudioUserInterface {
    fun updatePlayPauseButton()
    fun updateAudioTitle(title: String)
    fun syncSeekBarWithAudio(audio: Audio)
}