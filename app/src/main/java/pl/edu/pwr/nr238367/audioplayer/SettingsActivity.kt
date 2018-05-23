package pl.edu.pwr.nr238367.audioplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_settings.*

const val DEFAULT_TRANSITION_SPINNER_POSITION = 1

class SettingsActivity : AppCompatActivity() {
    private var audioServiceBinder: AudioService.ServiceBinder? = null
    private lateinit var serviceConnection: SettingsActivity.AudioServiceConnection
    private var serviceBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        //populate spinner values
        transitionSpinner.adapter = ArrayAdapter<TransitionType>(this, android.R.layout.simple_spinner_item, TransitionType.values())
        transitionSpinner.onItemSelectedListener = transitionSpinnerListener
        transitionSpinner.setSelection(DEFAULT_TRANSITION_SPINNER_POSITION)
    }

    override fun onStart() {
        super.onStart()
        bindToService()
    }

    override fun onStop() {
        unBindFromService()
        super.onStop()
    }

    private fun bindToService() {
        //bind to audioPlayer
        val intent = Intent(this, AudioService::class.java)
        serviceConnection = AudioServiceConnection()
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        serviceBound = true
    }

    private fun unBindFromService() {
        if (serviceBound) {
            unbindService(serviceConnection)
        }
        serviceBound = false
    }

    inner class AudioServiceConnection : ServiceConnection {
        override fun onServiceDisconnected(arg0: ComponentName?) {

        }

        override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
            audioServiceBinder = binder as AudioService.ServiceBinder


        }
    }

    private val transitionSpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parentView: AdapterView<*>?) {

        }

        override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
            audioServiceBinder?.apply {
                this.playbackManager.transitionType = TransitionType.values()[position]
            }
        }

    }
}

