package com.belchingjalapeno.agdqschedulenotifier

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class EventItem(private val events: Array<SpeedRunEvent>, private val workQueueManager: WorkQueueManager, private val eventFilter: EventFilter) : RecyclerView.Adapter<EventItem.ViewHolder>() {

    private val timeCalculator = TimeCalculator()
    private val backgroundColorSetter = BackgroundColorSetter()
    private val eventItemViewSetter = EventItemViewSetter()

    private val backingEventList = events.toMutableList()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val expandableView = LayoutInflater.from(p0.context)
                .inflate(R.layout.event_item, p0, false) as ExpandableConstraintLayout
        return ViewHolder(expandableView)
    }

    override fun getItemCount(): Int {
        return backingEventList.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        bind(p0, backingEventList[p1])
    }


    fun bind(viewHolder: ViewHolder, item: SpeedRunEvent) {
        viewHolder.apply {
            val currentTime = System.currentTimeMillis()
            val targetTime = timeCalculator.fromStringStartTimeToLong(item.startTime)
            val timeDifference = timeCalculator.getTimeDiff(currentTime, targetTime)
            //show more precise time as we get closer to the event
            val time: String = if (timeCalculator.getDays(timeDifference) <= 0) {
                if (timeCalculator.getHours(timeDifference) <= 0) {
                    timeCalculator.getFormattedTime(timeDifference, showMinutes = true, showSeconds = true)
                } else {
                    timeCalculator.getFormattedTime(timeDifference, showMinutes = true)
                }
            } else {
                timeCalculator.getFormattedTime(timeDifference, showHours = true)
            }

            startTimeView.text = "When   $time"

            val expectedLengthInMillis = timeCalculator.fromStringExpectedLengthToLong(item.runLength)
            val time2 = timeCalculator.getFormattedTime(expectedLengthInMillis, showHours = true, showMinutes = true, showSeconds = true)

            runLengthView.text = "Length   $time2"

            gameNameView.text = item.game
            categoryView.text = item.category
            castersView.text = item.runners
            runnersView.text = item.casters


            backgroundColorSetter.setColor(itemView, item, workQueueManager)

            eventItemViewSetter.setViewState(workQueueManager, itemView, item, 0)

            notificationIcon.setOnClickListener {
                //don't allow subscribing / unsubscribing if the filter is enabled
                if (eventFilter.notificationOnly) {
                    return@setOnClickListener
                }
                val queueManager = workQueueManager
                val isQueued = queueManager.isQueued(item)

                if (isQueued) {
                    queueManager.removeFromQueue(item)
                } else {
                    val currentTime = System.currentTimeMillis()
                    val targetTime = timeCalculator.fromStringStartTimeToLong(item.startTime)
                    val timeDifference = timeCalculator.getTimeDiff(currentTime, targetTime)

                    queueManager.addToQueue(item, timeDifference)
                }

                backgroundColorSetter.setColor(notificationIcon, item, queueManager)

                eventItemViewSetter.setViewState(queueManager, notificationIcon, item)
            }
        }
    }

    fun filter(notificationEnabled: Boolean, query: String) {
        val backingListCopy = backingEventList.toList()
        val filteredEvents = events.filter {
            if (notificationEnabled) {
                workQueueManager.isQueued(it)
            } else {
                true
            }
        }.filter {
            it.game.contains(query, ignoreCase = true)
        }
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                return backingListCopy[p0] === filteredEvents[p1]
            }

            override fun getOldListSize(): Int {
                return backingListCopy.size
            }

            override fun getNewListSize(): Int {
                return filteredEvents.size
            }

            override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                return backingListCopy[p0] == filteredEvents[p1]
            }
        }).dispatchUpdatesTo(this)
        backingEventList.clear()
        backingEventList.addAll(filteredEvents)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val gameNameView: TextView = itemView.findViewById(R.id.gameNameView)
        val startTimeView: TextView = itemView.findViewById(R.id.startTimeView)
        val runLengthView: TextView = itemView.findViewById(R.id.runLengthView)
        val categoryView: TextView = itemView.findViewById(R.id.categoryView)
        val castersView: TextView = itemView.findViewById(R.id.castersView)
        val runnersView: TextView = itemView.findViewById(R.id.runnersView)
        val expandableView: ExpandableConstraintLayout = itemView as ExpandableConstraintLayout
        val notificationIcon: ImageView = itemView.findViewById(R.id.notification_toggle_button)
    }
}