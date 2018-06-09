package com.belchingjalapeno.agdqschedulenotifier

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.SearchView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    //create the ItemAdapter holding your Items
    private val itemAdapter = ItemAdapter.items<EventItem>()
    //create the managing FastAdapter, by passing in the itemAdapter
    private val fastAdapter = FastAdapter.with<EventItem, ItemAdapter<EventItem>>(itemAdapter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val resource = resources.openRawResource(R.raw.events)
        var events = getEvents(BufferedReader(InputStreamReader(resource)))
        resource.close()

        val mutableListOf = mutableListOf<SpeedRunEvent>()
        mutableListOf.addAll(events)
        val time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault()).format(Date(System.currentTimeMillis() + (1000 * 60)))
        println("time : " + time)
        mutableListOf.add(0, SpeedRunEvent(time, "test", "test", "0:01:00", "test", "test", "0:01:00"))
        events = mutableListOf.toTypedArray()

        setupRecyclerView(events)
        setupSearchView()
    }

    private fun setupSearchView() {
        eventSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                itemAdapter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                itemAdapter.filter(newText)
                return false
            }
        })
    }

    private fun setupRecyclerView(events: Array<SpeedRunEvent>) {

        //set our adapters to the RecyclerView
        events_recycler_view.layoutManager = LinearLayoutManager(this)
        events_recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        events_recycler_view.adapter = fastAdapter

        val workManager = WorkQueueManager(getSharedPref(), resources.getColor(R.color.colorAccent), Color.WHITE, Color.LTGRAY)

        fastAdapter.withOnClickListener(EventItemClickListener(workManager))

        //set the items to your ItemAdapter
        itemAdapter.add(events.map { EventItem(it, workManager) })

        itemAdapter.itemFilter.withFilterPredicate({ item, constraint -> item.event.game.contains(constraint.toString(), ignoreCase = true) })
    }

    private fun getSharedPref(): SharedPreferences {
        return getSharedPreferences("enqueued", Context.MODE_PRIVATE)
    }
}