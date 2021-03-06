package com.belchingjalapeno.agdqschedulenotifier

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.belchingjalapeno.agdqschedulenotifier.ui.EventItemAdapter
import com.google.gson.Gson

private const val ARG_EVENTS = "arg_events"

class SpeedRunEventsFragment : Fragment() {

    private val changeListener = object : FilterChangedListener {
        override fun changed(notificationOnly: Boolean, query: String) {
            val activity = this@SpeedRunEventsFragment.activity
            if (activity != null) {
                eventItemAdapter.filter(notificationOnly, query, activity::runOnUiThread)
            }
        }
    }
    private lateinit var eventItemAdapter: EventItemAdapter
    private var events: Array<SpeedRunEvent> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            events = getEvents(it.getString(ARG_EVENTS, ""))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.speedrun_event_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view.findViewById(R.id.events_recycler_view), events)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, events: Array<SpeedRunEvent>) {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        val mainActivity = activity as MainActivity
        eventItemAdapter = EventItemAdapter(events, mainActivity.notificationQueue, mainActivity.subscribeFilter, this)
        recyclerView.adapter = eventItemAdapter

        recyclerView.setRecycledViewPool(mainActivity.recyclerViewPool)
    }

    override fun onStart() {
        super.onStart()

        val activity = activity as MainActivity
        activity.subscribeFilter.addListener(changeListener)
    }

    override fun onStop() {
        super.onStop()

        val activity = activity as MainActivity
        activity.subscribeFilter.removeListener(changeListener)
    }

    companion object {

        fun newInstance(events: Array<SpeedRunEvent>) =
                SpeedRunEventsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_EVENTS, Gson().toJson(events))
                    }
                }
    }
}
