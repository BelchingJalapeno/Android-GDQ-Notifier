package com.belchingjalapeno.agdqschedulenotifier

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class EventItem(val events: Array<SpeedRunEvent>, val workQueueManager: WorkQueueManager, val eventFilter: EventFilter) : RecyclerView.Adapter<EventItem.ViewHolder>() {

    private val timeCalculator = TimeCalculator()
    private val backgroundColorSetter = BackgroundColorSetter()
    private val eventItemViewSetter = EventItemViewSetter()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val expandableView = LayoutInflater.from(p0.context)
                .inflate(R.layout.event_item, p0, false) as ExpandableConstraintLayout
        return ViewHolder(expandableView)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        bind(p0, events[p1])
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

            expandableView.collapse(1)

            backgroundColorSetter.setColor(itemView, item, workQueueManager)

            eventItemViewSetter.setViewState(workQueueManager, itemView, item, 1)

            notificationIcon.setOnClickListener {
                //don't allow subscribing / unsubscribing if the filter is enabled
                if (eventFilter.notificationOnly) {
                    return@setOnClickListener
                }
                val queueManager = workQueueManager
                val event = item
                val isQueued = queueManager.isQueued(event)

                if (isQueued) {
                    queueManager.removeFromQueue(event)
                } else {
                    val currentTime = System.currentTimeMillis()
                    val targetTime = timeCalculator.fromStringStartTimeToLong(event.startTime)
                    val timeDifference = timeCalculator.getTimeDiff(currentTime, targetTime)

                    queueManager.addToQueue(event, timeDifference)
                }

                backgroundColorSetter.setColor(notificationIcon, item, queueManager)

                eventItemViewSetter.setViewState(queueManager, notificationIcon, item)
            }
        }
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