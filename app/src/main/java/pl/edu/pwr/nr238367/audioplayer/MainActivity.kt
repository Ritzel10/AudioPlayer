package pl.edu.pwr.nr238367.audioplayer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val audioList = listOf(Audio("Energy", "Bensound", 179, "bensoundenergy"))
    private lateinit var audioAdapter:AudioAdapter
    private lateinit var recyclerView:RecyclerView
    private lateinit var viewManager:RecyclerView.LayoutManager
    lateinit var playbackManager: PlaybackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewManager = LinearLayoutManager(this)
        audioAdapter = AudioAdapter(audioList, this)
        recyclerView = audioRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = audioAdapter
        }
        playbackManager = PlaybackManager(this)

    }


}
