package com.belchingjalapeno.agdqschedulenotifier

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class EventItem(val event: SpeedRunEvent, val workQueueManager: WorkQueueManager) : AbstractItem<EventItem, EventItem.ViewHolder>() {
    override fun getType(): Int {
        return R.id.event_item
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun getLayoutRes(): Int {
        return R.layout.event_item
    }

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<EventItem>(itemView) {

        private val gameNameView: TextView = itemView.findViewById(R.id.gameNameView)
        private val startTimeView: TextView = itemView.findViewById(R.id.startTimeView)
        private val setupTimeView: TextView = itemView.findViewById(R.id.setupTimeView)
        private val runLengthView: TextView = itemView.findViewById(R.id.runLengthView)
        private val castersView: TextView = itemView.findViewById(R.id.castersView)
        private val runnersView: TextView = itemView.findViewById(R.id.runnersView)
        private val categoryView: TextView = itemView.findViewById(R.id.categoryView)
        private val notificationToggleView: ImageView = itemView.findViewById(R.id.notification_toggle_button)

        private val timeCalculator = TimeCalculator()
        private val backgroundColorSetter = BackgroundColorSetter()

        override fun bindView(item: EventItem, payloads: MutableList<Any>?) {
            val currentTime = System.currentTimeMillis()
            val targetTime = timeCalculator.fromStringStartTimeToLong(item.event.startTime)
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

            startTimeView.text = time

            val expectedLengthInMillis = timeCalculator.fromStringExpectedLengthToLong(item.event.runLength)
            val time2 = timeCalculator.getFormattedTime(expectedLengthInMillis, showHours = true, showMinutes = true, showSeconds = true)

            runLengthView.text = time2


            gameNameView.text = item.event.game
            castersView.text = item.event.casters
            runnersView.text = item.event.runners
            setupTimeView.text = item.event.setupTime
            categoryView.text = item.event.category

            backgroundColorSetter.setColor(itemView, item.event, item.workQueueManager)

            val context = notificationToggleView.context
            if (item.workQueueManager.isQueued(item.event)) {
                notificationToggleView.setImageResource(R.drawable.ic_notifications_active_white_24dp)
                notificationToggleView.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
                notificationToggleView.imageAlpha = 255
            } else {
                notificationToggleView.setImageResource(R.drawable.ic_notifications_off_black_24dp)
                notificationToggleView.setColorFilter(0xFFFFFF)
                notificationToggleView.imageAlpha = (0.54f * 255).toInt()
            }
        }

        override fun unbindView(item: EventItem) {
        }
    }
}