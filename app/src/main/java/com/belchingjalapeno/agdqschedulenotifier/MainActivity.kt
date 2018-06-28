package com.belchingjalapeno.agdqschedulenotifier

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_CODE_ADD_EVENTS = 1
private const val REQUEST_CODE_REPLACE_EVENTS = 2

const val TWITCH_PREFERENCE_KEY = "TWITCH_PREFERENCE_KEY"
const val DONATE_PREFERENCE_KEY = "DONATE_PREFERENCE_KEY"

class MainActivity : AppCompatActivity() {

    lateinit var notificationQueue: NotificationQueue
    val subscribeFilter = EventFilter()
    //used by all RecyclerViews in fragments
    val recyclerViewPool = RecyclerView.RecycledViewPool()
    private lateinit var speedRunEventLoader: SpeedRunEventLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationQueue = NotificationQueue(this)
        speedRunEventLoader = SpeedRunEventLoader(this)

        val events = speedRunEventLoader.getEvents()

        setupTabs(events)
        setSupportActionBar(main_toolbar)
        setupDonateFab()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_actions, menu)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        val searchView = searchItem?.actionView as SearchView

        setupSearchView(searchView)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.app_bar_search -> {
            //already handled from searchview listener
            true
        }

        R.id.app_bar_twitch -> {
            startActivity(ExternalIntentsBuilder.getTwitchIntent(getSharedPref()))
            true
        }

        R.id.app_bar_subscribed -> {
            item.isChecked = !item.isChecked
            subscribeFilter.changeFilter(item.isChecked)
            true
        }

        R.id.app_bar_add_events -> {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setCancelable(true)
            dialogBuilder.setTitle("Add events")
            dialogBuilder.setMessage("This will keep your notifications active and add unique events to your current events. See file info in menu for more info.")
            dialogBuilder.setPositiveButton("Choose File", { dialogInterface: DialogInterface, i: Int ->
                startActivityForResult(ExternalIntentsBuilder.getJsonFilePickerIntent(), REQUEST_CODE_ADD_EVENTS)
            })
            dialogBuilder.setNegativeButton("Cancel", { dialogInterface: DialogInterface, i: Int -> })
            dialogBuilder.create().show()
            true
        }

        R.id.app_bar_replace_events -> {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setCancelable(true)
            dialogBuilder.setTitle("Replace all events")
            dialogBuilder.setMessage("This will cancel all notifications and replace all events. See file info in menu for more info.")
            dialogBuilder.setPositiveButton("Choose File", { dialogInterface: DialogInterface, i: Int ->
                startActivityForResult(ExternalIntentsBuilder.getJsonFilePickerIntent(), REQUEST_CODE_REPLACE_EVENTS)
            })
            dialogBuilder.setNegativeButton("Cancel", { dialogInterface: DialogInterface, i: Int -> })
            dialogBuilder.create().show()
            true
        }

        R.id.app_bar_file_format_info -> {
            val resource = resources.openRawResource(R.raw.json_sample)
            val html = (BufferedReader(InputStreamReader(resource))).readText()
            resource.close()

            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setCancelable(true)
            dialogBuilder.setTitle("File info")
            val frameView = FrameLayout(this)
            val webView = layoutInflater.inflate(R.layout.dialog_web_view, frameView, true).findViewById<WebView>(R.id.dialog_web_view)
            webView.loadData(html, "text/html", "utf8")
            dialogBuilder.setView(frameView)
            dialogBuilder.setPositiveButton("Got it", { dialogInterface: DialogInterface, i: Int ->
            })
            dialogBuilder.create().show()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            val eventsFileReader = InputStreamReader(contentResolver.openInputStream(uri))
            val newEventData = getEventData(eventsFileReader)
            if (newEventData.twitchUrl != null) {
                getSharedPref()
                        .edit()
                        .putString(TWITCH_PREFERENCE_KEY, newEventData.twitchUrl)
                        .apply()
            }
            if (newEventData.donateUrl != null) {
                getSharedPref()
                        .edit()
                        .putString(DONATE_PREFERENCE_KEY, newEventData.donateUrl)
                        .apply()
            }
            eventsFileReader.close()
            when (requestCode) {
                REQUEST_CODE_ADD_EVENTS -> {
                    val oldEvents = speedRunEventLoader.getEventData()

                    //remove duplicates
                    val events = (oldEvents.speedRunEvents + newEventData.speedRunEvents).toSet().toTypedArray()

                    val twitchUrl = newEventData.twitchUrl ?: oldEvents.twitchUrl
                    val donateUrl = newEventData.donateUrl ?: oldEvents.donateUrl
                    speedRunEventLoader.saveEvents(EventData(twitchUrl, donateUrl, events))
                }

                REQUEST_CODE_REPLACE_EVENTS -> {
                    notificationQueue.clearAll()
                    speedRunEventLoader.saveEvents(newEventData)
                }
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun setupTabs(events: Array<SpeedRunEvent>) {
        val eventsByDay = speedRunEventLoader.getEventsByDay(events)

        speedrun_viewpager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            private val fragmentList = eventsByDay.map { SpeedRunEventsFragment.newInstance(it) }
            private val simpleDateFormat = SimpleDateFormat("MMMM d", Locale.getDefault())

            override fun getItem(p0: Int): Fragment {
                return fragmentList[p0]
            }

            override fun getCount(): Int {
                return eventsByDay.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                val fromStringStartTimeToLong = eventsByDay[position][0].startTime
                return simpleDateFormat.format(Date(fromStringStartTimeToLong))
            }
        }

        val clamp = { value: Int -> Math.min(Math.max(eventsByDay.size - 1, value), 0) }

        val tabToSwitchTo = clamp(findCurrentDayTab(eventsByDay))
        speedrun_viewpager.setCurrentItem(tabToSwitchTo, false)

        tab_layout.setupWithViewPager(speedrun_viewpager, false)
    }

    //might get the day before current day if currently the next day but < first event startTime
    //but close enough for now
    private fun findCurrentDayTab(eventsByDay: Array<Array<SpeedRunEvent>>): Int {
        if (eventsByDay.isEmpty()) {
            return 0
        }
        val currentTime = System.currentTimeMillis()
        val day1 = eventsByDay[0]
        //if it isnt even the first day yet, return first day index
        if (day1[0].startTime > currentTime) {
            return 0
        }

        //find day we are currently on by seeing if the next day is > than the current time but
        // the current day is <= to the current time
        eventsByDay.fold(day1[0].startTime, { acc, arrayOfSpeedRunEvents ->
            if (acc <= currentTime && arrayOfSpeedRunEvents[0].startTime > currentTime) {
                return eventsByDay.indexOf(arrayOfSpeedRunEvents) - 1
            }
            arrayOfSpeedRunEvents[0].startTime
        })

        //if all the events have already happened, return the last day
        return eventsByDay.size
    }

    private fun setupDonateFab() {
        donate_fab.setOnClickListener { startActivity(ExternalIntentsBuilder.getDonateIntent(getSharedPref())) }
    }

    private fun setupSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                query(newText)
                return false
            }

            private fun query(query: String?) {
                if (query != null) {
                    subscribeFilter.changeFilter(query)
                }
            }
        })
    }

    private fun getSharedPref(): SharedPreferences {
        return applicationContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }
}