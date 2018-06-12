package com.belchingjalapeno.agdqschedulenotifier

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.expandable.ExpandableExtension
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

    private lateinit var workQueueManager: WorkQueueManager
    private lateinit var searchView: SearchView
    private val subscribeFilter = SubscribedFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workQueueManager = WorkQueueManager(getSharedPref(), 0, 0, 0)

        val resource = resources.openRawResource(R.raw.events)
        var events = getEvents(BufferedReader(InputStreamReader(resource)))
        resource.close()

        val mutableListOf = mutableListOf<SpeedRunEvent>()
        mutableListOf.addAll(events)
        val time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault()).format(Date(System.currentTimeMillis() + (1000 * 60)))
        mutableListOf.add(0, SpeedRunEvent(time, "test", "test", "0:01:00", "test", "test", "0:01:00"))
        events = mutableListOf.toTypedArray()

        setupRecyclerView(events)
        setSupportActionBar(main_toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_actions, menu)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        searchView = searchItem?.actionView as SearchView

        setupSearchView(searchView)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.app_bar_search -> {
            //already handled from searchview listener
            true
        }

        R.id.app_bar_twitch -> {
            startActivity(TwitchIntentBuilder.getTwitchIntent())
            true
        }

        R.id.app_bar_subscribed -> {
            item.isChecked = !item.isChecked
            subscribeFilter.enabled = item.isChecked
            //add space for filter not getting called work around
            itemAdapter.filter(" " + searchView.query)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        //add space for before query, trimmed off durring the filter
        //used for a work around with the filter not getting called when the string is empty
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                itemAdapter.filter(" " + query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                itemAdapter.filter(" " + newText)
                return false
            }
        })
    }

    private fun setupRecyclerView(events: Array<SpeedRunEvent>) {

        //set our adapters to the RecyclerView
        events_recycler_view.layoutManager = LinearLayoutManager(this)
        events_recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        events_recycler_view.adapter = fastAdapter

        val workManager = WorkQueueManager(getSharedPref(), ContextCompat.getColor(this, R.color.colorAccent), Color.WHITE, Color.LTGRAY)

        fastAdapter.withOnClickListener(EventItemClickListener(workManager, subscribeFilter, EventItemViewSetter(workQueueManager)))

        //set the items to your ItemAdapter
        itemAdapter.add(events.map { EventItem(it, workManager) })

        itemAdapter.itemFilter.withFilterPredicate({ item, constraint ->
            //remove space that is added at the start for filter work around
            val trimmedConstraint = constraint?.substring(1)
            item.event.game.contains(trimmedConstraint.toString(), ignoreCase = true) &&
                    if (subscribeFilter.enabled) {
                        isSubscribed(item.event)
                    } else {
                        true
                    }
        })
    }

    private fun isSubscribed(event: SpeedRunEvent): Boolean {
        return workQueueManager.isQueued(event)
    }

    private fun getSharedPref(): SharedPreferences {
        return getSharedPreferences("enqueued", Context.MODE_PRIVATE)
    }
}