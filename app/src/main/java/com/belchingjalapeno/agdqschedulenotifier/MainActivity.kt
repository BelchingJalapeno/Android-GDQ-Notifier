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
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


private const val REQUEST_CODE_ADD_EVENTS = 1
private const val REQUEST_CODE_REPLACE_EVENTS = 2

const val TWITCH_PREFERENCE_KEY = "TWITCH_PREFERENCE_KEY"
const val DONATE_PREFERENCE_KEY = "DONATE_PREFERENCE_KEY"

class MainActivity : AppCompatActivity() {

    lateinit var notificationQueue: NotificationQueue
    private var searchView: SearchView? = null
    val subscribeFilter = EventFilter()
    val recyclerViewPool = RecyclerView.RecycledViewPool()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationQueue = NotificationQueue(this)

        val eventsFile = getEventsFile()
        var events = if (eventsFile.exists()) {
            val fileReader = FileReader(eventsFile)
            val eventData = getEventData(fileReader)
            fileReader.close()
            eventData.speedRunEvents
        } else {
            val defaultEventData = getDefaultEventData()
            saveEvents(eventsFile, defaultEventData)
            defaultEventData.speedRunEvents
        }

        val mutableListOf = mutableListOf<SpeedRunEvent>()
        mutableListOf.addAll(events)
//        mutableListOf.add(0, SpeedRunEvent(System.currentTimeMillis() + 60 * 1000L, "test1", "test", "0:01:00", "test", "test", "0:01:00"))
//        mutableListOf.add(1, SpeedRunEvent(System.currentTimeMillis() + 90 * 1000L, "test2", "test", "0:01:00", "test", "test", "0:01:00"))
        events = mutableListOf.toTypedArray()

        setupTabs(events)
        setSupportActionBar(main_toolbar)
        setupDonateFab()
    }

    private fun getEventsFile() = File(filesDir, "events.json")

    private fun saveEvents(eventsFile: File, eventData: EventData) {
        val fileWriter = FileWriter(eventsFile)
        fileWriter.write(eventDataToJsonString(eventData))
        fileWriter.close()
    }

    private fun getDefaultEventData(): EventData {
        val resource = resources.openRawResource(R.raw.events)
        val eventData = getEventData(BufferedReader(InputStreamReader(resource)))
        resource.close()
        return eventData
    }

    private fun setupTabs(events: Array<SpeedRunEvent>) {
        val eventsByDay = getEventsByDay(events)
        tab_layout.setupWithViewPager(speedrun_viewpager, false)
        val fragmentList = eventsByDay.map { SpeedRunEventsFragment.newInstance(it) }
        speedrun_viewpager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(p0: Int): Fragment {
                return fragmentList[p0]
            }

            override fun getCount(): Int {
                return eventsByDay.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                val fromStringStartTimeToLong = eventsByDay[position][0].startTime
                return SimpleDateFormat("MMMM d", Locale.getDefault()).format(Date(fromStringStartTimeToLong))
            }
        }
    }

    private fun getEventsByDay(events: Array<SpeedRunEvent>): Array<Array<SpeedRunEvent>> {
        events.sortBy { it.startTime }
        val groupBy = events.groupBy({
            val date = Date(it.startTime)
            SimpleDateFormat.getDateInstance().format(date)
        })
        val v = groupBy.values.map { it.toTypedArray() }
        return v.toTypedArray()
    }

    private fun setupDonateFab() {
        donate_fab.setOnClickListener { startActivity(ExternalIntentsBuilder.getDonateIntent(getSharedPref())) }
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
            startActivity(ExternalIntentsBuilder.getTwitchIntent(getSharedPref()))
            true
        }

        R.id.app_bar_subscribed -> {
            item.isChecked = !item.isChecked
            val query = searchView?.query?.toString() ?: ""
            subscribeFilter.changeFilter(item.isChecked, query)

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
                    val fileReader = FileReader(getEventsFile())
                    val oldEvents = getEventData(fileReader)
                    fileReader.close()

                    //remove duplicates
                    val events = (oldEvents.speedRunEvents + newEventData.speedRunEvents).toSet().toTypedArray()

                    val twitchUrl = newEventData.twitchUrl ?: oldEvents.twitchUrl
                    val donateUrl = newEventData.donateUrl ?: oldEvents.donateUrl
                    saveEvents(getEventsFile(), EventData(twitchUrl, donateUrl, events))
                }

                REQUEST_CODE_REPLACE_EVENTS -> {
                    notificationQueue.clearAll()
                    saveEvents(getEventsFile(), newEventData)
                }
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun setupSearchView(searchView: SearchView) {
        //add space for before query, trimmed off durring the filter
        //used for a work around with the filter not getting called when the string is empty
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

    fun isSubscribed(event: SpeedRunEvent): Boolean {
        return notificationQueue.isQueued(event)
    }

    fun getSharedPref(): SharedPreferences {
        return applicationContext.getSharedPreferences("enqueued", Context.MODE_PRIVATE)
    }
}