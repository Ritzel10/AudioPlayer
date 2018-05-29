package pl.edu.pwr.nr238367.audioplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.defaultSharedPreferences


const val FRAGMENT_NAME = "settings_fragment"
const val DEFAULT_TRANSITION_SPINNER_POSITION = 1
const val FOLDER_RESULT_ID = 200

class SettingsActivity : AppCompatActivity() {


    var audioServiceBinder: AudioService.ServiceBinder? = null
    private lateinit var serviceConnection: SettingsActivity.AudioServiceConnection
    private var serviceBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        //display back button and disable title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }
        fragmentManager.beginTransaction().replace(R.id.container, SettingsFragment(), FRAGMENT_NAME).commit()

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

}

class SettingsFragment : PreferenceFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        //fill preference values
        val transitionPreference = findPreference(getString(R.string.transition_key)) as ListPreference
        transitionPreference.entries = TransitionType.values().map { it.toString() }.toTypedArray()
        transitionPreference.entryValues = (0 until TransitionType.values().size).map { it.toString() }.toTypedArray()
        transitionPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, value ->
            onTransitionChanged(value)
            true
        }
        val folderPreference = findPreference(getString(R.string.folder_key))
        folderPreference.setOnPreferenceClickListener {
            chooseAudioFolder()
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FOLDER_RESULT_ID) {
            data?.apply {
                AudioFolder.initDocumentFile(this.data, activity)
                //put path to shared preferences
                val editor = defaultSharedPreferences.edit()
                editor.putString(getString(R.string.folder_key), this.data.convertDocumentPath())
                editor.apply()
            }

        }
    }

    private fun chooseAudioFolder() {
        val folderChooserIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        folderChooserIntent.addCategory(Intent.CATEGORY_DEFAULT)
        startActivityForResult(Intent.createChooser(folderChooserIntent, getString(R.string.folder_choose_message)), FOLDER_RESULT_ID)
    }

    private fun onTransitionChanged(value: Any) {
        val position = Integer.valueOf(value as String)
        (activity as SettingsActivity).audioServiceBinder?.audioSettings?.changeServiceTransitionType(Integer.valueOf(position))
    }
}


