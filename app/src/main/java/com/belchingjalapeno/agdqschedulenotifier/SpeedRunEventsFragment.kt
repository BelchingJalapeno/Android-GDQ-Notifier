package com.belchingjalapeno.agdqschedulenotifier

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson

private const val ARG_EVENTS = "param1"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SpeedRunEventsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SpeedRunEventsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SpeedRunEventsFragment : Fragment() {

    private val changeListener = object : FilterChangedListener {
        override fun changed(notificationOnly: Boolean, query: String) {
            eventItem.filter(notificationOnly, query)
        }
    }
    private lateinit var eventItem: EventItem
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
        //set our adapters to the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        val mainActivity = activity as MainActivity
        val workManager = mainActivity.workQueueManager
        eventItem = EventItem(events, workManager, mainActivity.subscribeFilter)
        recyclerView.adapter = eventItem

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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SpeedRunEventsFragment.
         */
        @JvmStatic
        fun newInstance(events: Array<SpeedRunEvent>) =
                SpeedRunEventsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_EVENTS, Gson().toJson(events))
                    }
                }
    }
}
