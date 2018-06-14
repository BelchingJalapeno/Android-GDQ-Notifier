package com.belchingjalapeno.agdqschedulenotifier

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var workQueueManager: WorkQueueManager
    private var searchView: SearchView? = null
    val subscribeFilter = SubscribedFilter()
    private var currentFragment: SpeedrunEventsFragment? = null

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

        setupTabs(events)
        setSupportActionBar(main_toolbar)
        setupDonateFab()
    }

    private fun setupTabs(events: Array<SpeedRunEvent>) {
        val eventsByDay = getEventsByDay(events)
        tab_layout.setupWithViewPager(speedrun_viewpager)
        speedrun_viewpager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(p0: Int): Fragment {
                return SpeedrunEventsFragment.newInstance(eventsByDay[p0])
            }

            override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
                super.setPrimaryItem(container, position, `object`)
                currentFragment = `object` as SpeedrunEventsFragment
                if(searchView != null){
                    getCurrentFragment()?.itemAdapter?.filter(" " + searchView?.query)
                }
            }

            override fun getCount(): Int {
                return eventsByDay.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                val fromStringStartTimeToLong = TimeCalculator().fromStringStartTimeToLong(eventsByDay[position][0].startTime)
                return SimpleDateFormat.getDateInstance().format(Date(fromStringStartTimeToLong))
            }
        }
    }

    private fun getEventsByDay(events: Array<SpeedRunEvent>): Array<Array<SpeedRunEvent>> {
        val calc = TimeCalculator()
        events.sortBy { calc.fromStringStartTimeToLong(it.startTime) }
        val groupBy = events.groupBy({
            val date = Date(calc.fromStringStartTimeToLong(it.startTime))
            SimpleDateFormat.getDateInstance().format(date)
        })
        val v = groupBy.values.map { it.toTypedArray() }
        return v.toTypedArray()
    }

    private fun setupDonateFab() {
        donate_fab.setOnClickListener { startActivity(TwitchIntentBuilder.getDonateIntent()) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_actions, menu)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        searchView = searchItem?.actionView as SearchView

        setupSearchView(searchView!!)

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
            getCurrentFragment()?.itemAdapter?.filter(" " + searchView?.query)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun getCurrentFragment(): SpeedrunEventsFragment? {
        return currentFragment
    }

    private fun setupSearchView(searchView: SearchView) {
        //add space for before query, trimmed off durring the filter
        //used for a work around with the filter not getting called when the string is empty
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                getCurrentFragment()?.itemAdapter?.filter(" " + query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getCurrentFragment()?.itemAdapter?.filter(" " + newText)
                return false
            }
        })
    }

    fun isSubscribed(event: SpeedRunEvent): Boolean {
        return workQueueManager.isQueued(event)
    }

    fun getSharedPref(): SharedPreferences {
        return getSharedPreferences("enqueued", Context.MODE_PRIVATE)
    }
}