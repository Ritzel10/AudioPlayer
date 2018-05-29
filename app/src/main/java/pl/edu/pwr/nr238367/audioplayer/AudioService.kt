package pl.edu.pwr.nr238367.audioplayer

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.LocalBroadcastManager


const val INTENT_FILTER_SERVICE_COMMUNICATION = "intent_filter_service_communication"
const val FOREGROUND_ID = 1908
const val NOTIFICATION_CHANNEL = "notification_audio"
const val PAUSE_REQUEST_CODE = 0
const val NEXT_REQUEST_CODE = 1
const val RESTART_REQUEST_CODE = 2
const val ACTIVITY_REQUEST_CODE = 3
const val ACTION_NAME = "action"
const val ACTION_PLAY_PAUSE = "play_pause"
const val ACTION_NEXT = "next"
const val ACTION_RESTART = "restart"


class AudioService : Service(), AudioForegroundService {


    lateinit var playbackManager: PlaybackManager
    lateinit var audioSettings: AudioSettings
    //broadcast receiver that receives messages from notification buttons
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra(ACTION_NAME)
            when (message) {
                ACTION_PLAY_PAUSE -> {
                    if (playbackManager.isPlaying) {
                        playbackManager.pause()
                    } else {
                        playbackManager.resume()

                    }
                    notifyActivity()
                }
                ACTION_NEXT -> {
                    playbackManager.nextSong()
                    notifyActivity()
                }
                ACTION_RESTART -> {
                    playbackManager.seekToSeconds(0)
                    notifyActivity()
                }
            }
        }
    }
    private fun notifyActivity() {
        val intentForActivity = Intent(INTENT_FILTER_ACTIVITY_COMMUNICATION)
        intentForActivity.putExtra(MESSAGE_NAME, NowPlaying(playbackManager.currentlyPlaying!!))
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentForActivity)
        updateNotification(playbackManager.currentlyPlaying!!)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FOREGROUND_ID, createNotification())
        registerReceiver(broadcastReceiver, IntentFilter(INTENT_FILTER_SERVICE_COMMUNICATION))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
    private fun createNotification(audio: Audio? = null): Notification {
        val intentPause = Intent(INTENT_FILTER_SERVICE_COMMUNICATION)
        intentPause.putExtra(ACTION_NAME, ACTION_PLAY_PAUSE)
        val intentNext = Intent(INTENT_FILTER_SERVICE_COMMUNICATION)
        intentNext.putExtra(ACTION_NAME, ACTION_NEXT)
        val intentRestart = Intent(INTENT_FILTER_SERVICE_COMMUNICATION)
        intentRestart.putExtra(ACTION_NAME, ACTION_RESTART)
        val intentActivity = Intent(this, MainActivity::class.java)
        val openActivityIntent = PendingIntent.getActivity(this, ACTIVITY_REQUEST_CODE, intentActivity, PendingIntent.FLAG_ONE_SHOT)
        val pausePendingIntent = PendingIntent.getBroadcast(this, PAUSE_REQUEST_CODE, intentPause, PendingIntent.FLAG_UPDATE_CURRENT)
        val nextPendingIntent = PendingIntent.getBroadcast(this, NEXT_REQUEST_CODE, intentNext, PendingIntent.FLAG_UPDATE_CURRENT)
        val restartPendingIntent = PendingIntent.getBroadcast(this, RESTART_REQUEST_CODE, intentRestart, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
                .setContentTitle(audio?.title ?: getString(R.string.app_name))
                .setContentText(audio?.author ?: "")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(openActivityIntent)
        if (this::playbackManager.isInitialized && playbackManager.isStarted) {
            builder.addAction(R.drawable.ic_skip_previous_black_24dp, getString(R.string.notification_restart), restartPendingIntent)
            if (playbackManager.isPlaying) {
                builder.addAction(R.drawable.ic_pause_black_24dp, getString(R.string.notification_pause), pausePendingIntent)
            } else {
                builder.addAction(R.drawable.ic_play_arrow_black_24dp, getString(R.string.notification_play), pausePendingIntent)
            }
            builder.addAction(R.drawable.ic_skip_next_black_24dp, getString(R.string.notification_next), nextPendingIntent).setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2))
        }
        return builder.build()
    }

    override fun updateNotification(audio: Audio) {
        NotificationManagerCompat.from(this).notify(FOREGROUND_ID, createNotification(audio))
    }

    fun refreshNotification() {
        playbackManager.currentlyPlaying?.apply {
            updateNotification(this)
        }

    }
    override fun onBind(intent: Intent): IBinder? {
        playbackManager = PlaybackManager(this, this)
        audioSettings = AudioSettings(this, playbackManager)
        audioSettings.loadSettings()
        return ServiceBinder()
    }

    inner class ServiceBinder : Binder() {
        val playbackManager: PlaybackManager get() = this@AudioService.playbackManager
        val audioSettings: AudioSettings get() = this@AudioService.audioSettings
        fun refreshNotification() {
            this@AudioService.refreshNotification()
        }
    }
}