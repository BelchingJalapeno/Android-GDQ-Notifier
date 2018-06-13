package com.belchingjalapeno.agdqschedulenotifier

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_EVENTS = "param1"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SpeedrunEventsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SpeedrunEventsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SpeedrunEventsFragment : Fragment() {

    //create the ItemAdapter holding your Items
    val itemAdapter = ItemAdapter.items<EventItem>()
    //create the managing FastAdapter, by passing in the itemAdapter
    private val fastAdapter = FastAdapter.with<EventItem, ItemAdapter<EventItem>>(itemAdapter)

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
        recyclerView.adapter = fastAdapter

        val mainActivity = activity as MainActivity
        val workManager = WorkQueueManager(mainActivity.getSharedPref(), ContextCompat.getColor(mainActivity, R.color.colorAccent), Color.WHITE, Color.LTGRAY)

        fastAdapter.withOnClickListener(EventItemClickListener(workManager, mainActivity.subscribeFilter, EventItemViewSetter(mainActivity.workQueueManager)))

        //set the items to your ItemAdapter
        itemAdapter.add(events.map { EventItem(it, workManager) })

        itemAdapter.itemFilter.withFilterPredicate({ item, constraint ->
            //remove space that is added at the start for filter work around
            val trimmedConstraint = constraint?.substring(1)
            item.event.game.contains(trimmedConstraint.toString(), ignoreCase = true) &&
                    if (mainActivity.subscribeFilter.enabled) {
                        mainActivity.isSubscribed(item.event)
                    } else {
                        true
                    }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SpeedrunEventsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(events: Array<SpeedRunEvent>) =
                SpeedrunEventsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_EVENTS, Gson().toJson(events))
                    }
                }
    }
}
