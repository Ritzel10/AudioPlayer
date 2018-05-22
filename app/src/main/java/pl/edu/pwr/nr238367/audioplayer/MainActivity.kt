package pl.edu.pwr.nr238367.audioplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_main.*
import pl.edu.pwr.nr238367.audioplayer.R.layout.audio_controls

class MainActivity : AppCompatActivity() {
    val audioList = listOf(Audio("Energy", "Bensound", 179, "bensoundenergy"))
    private lateinit var audioAdapter: AudioAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var serviceConnection: AudioServiceConnection
    private var audioService: AudioService.ServiceBinder? = null
    private var serviceBound = false
    private lateinit var mediaController: MediaController
    //lateinit var playbackManager: PlaybackManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //bind to service
        val intent = Intent(this, AudioService::class.java)
        serviceConnection = AudioServiceConnection()
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        audioAdapter = AudioAdapter(audioList, this)
        viewManager = LinearLayoutManager(this)
        recyclerView = audioRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = audioAdapter
        }
    }
//        playPause.setOnClickListener {
//            playPauseOnClick()
//        }
//        skipBackwards.setOnClickListener {
//            audioService?.playbackManager?.skipBackwards()
//        }
//        skipForwards.setOnClickListener {
//            audioService?.playbackManager?.skipForwards()
//        }
//    }

//
//    fun playPauseOnClick() {
//        if (audioService?.playbackManager?.isPlaying == true) {
//            audioService?.playbackManager?.pause()
//        } else {
//            audioService?.playbackManager?.resume()
//        }
//        updatePlayPauseButton()
//    }
//
//    fun updatePlayPauseButton() {
//        if (audioService?.playbackManager?.isPlaying == true) {
//            playPause.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_pause_black_24dp))
//        } else {
//            playPause.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_play_arrow_black_24dp))
//        }
//    }
//
//
//    fun changeSongTitle(title: String) {
//        audioControls.audioTitle.text = title
//    }

    inner class AudioServiceConnection : ServiceConnection {
        override fun onServiceDisconnected(arg0: ComponentName?) {
            serviceBound = false
        }

        override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
            audioService = binder as AudioService.ServiceBinder
            audioAdapter.service = audioService
            mediaController = MediaController(this@MainActivity)
            mediaController.setMediaPlayer(AudioController(binder.mediaPlayer))
            mediaController.setAnchorView(this@MainActivity.mainLayout)
            mediaController.isEnabled = true
            try {
                mediaController.show(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            serviceBound = true
        }
    }

    companion object {
        fun getView(): Int = audio_controls
    }
}
