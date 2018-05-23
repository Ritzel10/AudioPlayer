package pl.edu.pwr.nr238367.audioplayer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.audio_row.view.*

class AudioAdapter(private val dataSet: List<Audio>, private val userInterface: AudioUserInterface, var audioPlayer: PlaybackManager? = null) : RecyclerView.Adapter<AudioAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rowView = holder.rowView
        val audio = dataSet[position]
        rowView.audioTitle.text = audio.title
        rowView.audioAuthor.text = audio.author
        rowView.audioDuration.text = audio.durationToString
        rowView.imageButton.setOnClickListener { playAudio(position) }
    }

    private fun playAudio(position: Int) {
        audioPlayer?.startAudio(position)
        val audio = dataSet[position]
        userInterface.showControls()
        userInterface.updateAudioTitle(audio.title)
        userInterface.updatePlayPauseButton()
        userInterface.syncSeekBarWithAudio(audio)
    }
    override fun getItemCount(): Int {
        return dataSet.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.audio_row, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(val rowView: View):RecyclerView.ViewHolder(rowView)
}