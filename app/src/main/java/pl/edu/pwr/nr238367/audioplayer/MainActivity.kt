package pl.edu.pwr.nr238367.audioplayer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.Duration

class MainActivity : AppCompatActivity() {
    val audioList = listOf(Audio("tytuł", "autor", 180))
    private lateinit var audioAdapter:AudioAdapter
    private lateinit var recyclerView:RecyclerView
    private lateinit var viewManager:RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        viewManager = LinearLayoutManager(this)
        audioAdapter = AudioAdapter(audioList, applicationContext)
        recyclerView = audioRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = audioAdapter
        }
    }
}
