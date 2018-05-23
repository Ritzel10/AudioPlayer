package pl.edu.pwr.nr238367.audioplayer

import android.content.*
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.audio_controls.*
import kotlinx.android.synthetic.main.audio_controls.view.*

const val CHECK_DELAY: Long = 100
const val MESSAGE_NAME = "MESSAGE"
const val INTENT_FILTER_ACTIVITY_COMMUNICATION = "intent_filter"

class MainActivity : AppCompatActivity(), AudioUserInterface {


    //    val audioList = listOf(Audio("Energy", "Bensound", 179, "bensoundenergy"), Audio("All that", "Bensound", 145, "allthat"))
    private lateinit var audioAdapter: AudioAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var serviceConnection: AudioServiceConnection
    private var audioService: AudioService.ServiceBinder? = null
    private var serviceBound = false
    private lateinit var handler: Handler
    private lateinit var seekBarListener: SeekBarAudioControlListener
    private var currentFolder: String = "Music"
    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.extras?.getParcelable<Message>(MESSAGE_NAME)
            when (message) {
                is AudioFinished -> updatePlayPauseButton()
                is NowPlaying -> {
                    updateCurrentAudio(message.playing)
                }
            }
        }

    }
    //lateinit var playbackManager: PlaybackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        AudioFolder.init("${Environment.getExternalStorageDirectory()}/$currentFolder", this)
        viewManager = LinearLayoutManager(this)
        audioAdapter = AudioAdapter(AudioFolder.audioList, this)
        recyclerView = audioRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = audioAdapter
        }
        playPause.setOnClickListener {
            playPauseOnClick()
        }
        skipBackwards.setOnClickListener {
            audioService?.playbackManager?.skipBackwards()
        }
        skipForwards.setOnClickListener {
            audioService?.playbackManager?.skipForwards()
        }
        seekBarListener = SeekBarAudioControlListener()
        seekBar.setOnSeekBarChangeListener(seekBarListener)

        startService()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter(INTENT_FILTER_ACTIVITY_COMMUNICATION))
        handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateSeekBar()
                handler.postDelayed(this, CHECK_DELAY)
            }

        }, CHECK_DELAY)
    }

    override fun onStart() {
        super.onStart()
        bindToService()
    }

    override fun onStop() {
        unBindFromService()
        super.onStop()
    }

    private fun startService() {
        val intent = Intent(this, AudioService::class.java)
        startService(intent)
    }

    private fun bindToService() {
        //bind to audioPlayer
        val intent = Intent(this, AudioService::class.java)
        serviceConnection = AudioServiceConnection()
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unBindFromService() {
        if (serviceBound) {
            unbindService(serviceConnection)
        }
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
        handler.removeCallbacksAndMessages(null)
        super.onPause()
    }

    private fun playPauseOnClick() {
        if (audioService?.playbackManager?.isPlaying == true) {
            audioService?.playbackManager?.pause()
        } else {
            audioService?.playbackManager?.resume()
        }
        updatePlayPauseButton()
        audioService?.refreshNotification()
    }

    private fun updateSeekBar() {
        val currentTime = audioService?.playbackManager?.currentTime
        seekBar.progress = currentTime ?: 0
//        Log.i("SEEKBAR", currentTime.toString())
    }

    private fun updateCurrentAudio(audio: Audio) {
        updateAudioTitle(audio.title)
        syncSeekBarWithAudio(audio)
        updateSeekBar()
        updatePlayPauseButton()
    }

    override fun updatePlayPauseButton() {
        if (audioService?.playbackManager?.isPlaying == true) {
            playPause.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_pause_black_24dp))
        } else {
            playPause.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_play_arrow_black_24dp))
        }
    }


    override fun updateAudioTitle(title: String) {
        audioControls.audioTitle.text = title
    }

    override fun syncSeekBarWithAudio(audio: Audio) {
        seekBar.progress = 0
        seekBar.max = audio.durationSeconds

    }
    inner class AudioServiceConnection : ServiceConnection {
        override fun onServiceDisconnected(arg0: ComponentName?) {
            serviceBound = false
        }

        override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
            audioService = binder as AudioService.ServiceBinder
            audioAdapter.audioPlayer = audioService?.playbackManager
            seekBarListener.playbackManager = audioService?.playbackManager
            serviceBound = true
            val audio = binder.playbackManager.currentlyPlaying
            audio?.let {
                updateCurrentAudio(audio)
            }
        }

    }
}
