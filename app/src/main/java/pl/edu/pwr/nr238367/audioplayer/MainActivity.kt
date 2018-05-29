package pl.edu.pwr.nr238367.audioplayer

import android.content.*
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.audio_controls.*
import kotlinx.android.synthetic.main.audio_controls.view.*
import org.jetbrains.anko.startActivity

const val CHECK_DELAY: Long = 100
const val MESSAGE_NAME = "MESSAGE"
const val INTENT_FILTER_ACTIVITY_COMMUNICATION = "intent_filter"

class MainActivity : AppCompatActivity(), AudioUserInterface {



    private lateinit var audioAdapter: AudioAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var serviceConnection: AudioServiceConnection
    private var audioServiceBinder: AudioService.ServiceBinder? = null
    private var serviceBound = false
    private lateinit var handler: Handler
    private lateinit var seekBarListener: SeekBarAudioControlListener

    //broadcast receiver that receives messages from the service
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        loadFolder()
        //initialize audio files from folder
//        AudioFolder.initDocumentFile("${Environment.getExternalStorageDirectory()}/$currentFolder", this)

        viewManager = LinearLayoutManager(this)
        audioAdapter = AudioAdapter(AudioFolder.audioList, this)
        recyclerView = audioRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = audioAdapter
        }
        //add logic to audio control buttons
        playPause.setOnClickListener {
            playPauseOnClick()
        }
        skipBackwards.setOnClickListener {
            audioServiceBinder?.playbackManager?.skipBackwards()
        }
        skipForwards.setOnClickListener {
            audioServiceBinder?.playbackManager?.skipForwards()
        }
        seekBarListener = SeekBarAudioControlListener()
        seekBar.setOnSeekBarChangeListener(seekBarListener)
        startService()


    }

    private fun loadFolder() {
        //load folder from shared preferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        //default path is the root folder
        val path = sharedPreferences.getString(getString(R.string.folder_key), Environment.getExternalStorageDirectory().absolutePath)
        AudioFolder.initDocumentFile(path, this)
    }
    override fun onResume() {
        super.onResume()
        //get current audio status from service
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter(INTENT_FILTER_ACTIVITY_COMMUNICATION))
        handler = Handler()
        //show current audio time
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateSeekBar()
                handler.postDelayed(this, CHECK_DELAY)
            }

        }, CHECK_DELAY)
        audioAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        bindToService()

    }

    override fun onStop() {
        unBindFromService()
        super.onStop()
    }

    //inflate toolbar menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
        //show settings
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
                true
            }
            R.id.action_about -> {
                startActivity<AboutActivity>()
                true
            }
            else ->
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
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

    //resume or stop the audio depending on state
    private fun playPauseOnClick() {
        if (audioServiceBinder?.playbackManager?.isPlaying == true) {
            audioServiceBinder?.playbackManager?.pause()
        } else {
            audioServiceBinder?.playbackManager?.resume()
        }
        updatePlayPauseButton()
        audioServiceBinder?.refreshNotification()
    }


    override fun showControls() {
        audioControls.visibility = View.VISIBLE
    }

    private fun updateSeekBar() {
        val currentTime = audioServiceBinder?.playbackManager?.currentTime
        seekBar.progress = currentTime ?: 0
//        Log.i("SEEKBAR", currentTime.toString())
    }

    //update info about current audio (title, current time)
    private fun updateCurrentAudio(audio: Audio) {
        updateAudioTitle(audio.title)
        syncSeekBarWithAudio(audio)
        updateSeekBar()
        updatePlayPauseButton()
    }

    //display play or pause icon depending on audio state
    override fun updatePlayPauseButton() {
        if (audioServiceBinder?.playbackManager?.isPlaying == true) {
            playPause.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_pause))
        } else {
            playPause.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_play))
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
            audioServiceBinder = binder as AudioService.ServiceBinder
            audioAdapter.audioPlayer = audioServiceBinder?.playbackManager
            seekBarListener.playbackManager = audioServiceBinder?.playbackManager
            serviceBound = true
            val audio = binder.playbackManager.currentlyPlaying
            audio?.let {
                updateCurrentAudio(audio)
            }
            if (audioServiceBinder?.playbackManager?.isStarted == true) {
                showControls()
                audioServiceBinder?.refreshNotification()
            }
        }
    }
}
