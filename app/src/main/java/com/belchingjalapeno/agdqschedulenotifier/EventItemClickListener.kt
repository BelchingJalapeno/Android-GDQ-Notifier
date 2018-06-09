package com.belchingjalapeno.agdqschedulenotifier

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.listeners.OnClickListener

class EventItemClickListener(private val queueManager: WorkQueueManager, private val subscribedFilter: SubscribedFilter) : OnClickListener<EventItem> {
    private val timeCalculator = TimeCalculator()
    private val backgroundColorSetter = BackgroundColorSetter()

    override fun onClick(v: View?, adapter: IAdapter<EventItem>?, item: EventItem, position: Int): Boolean {
        //don't allow subscribing / unsubscribing if the filter is enabled
        if (subscribedFilter.enabled) {
            return true
        }
        val event = item.event
        val isQueued = queueManager.isQueued(event)

        if (isQueued) {
            queueManager.removeFromQueue(event)
        } else {
            val currentTime = System.currentTimeMillis()
            val targetTime = timeCalculator.fromStringStartTimeToLong(event.startTime)
            val timeDifference = timeCalculator.getTimeDiff(currentTime, targetTime)

            queueManager.addToQueue(event, timeDifference)
        }

        backgroundColorSetter.setColor(v, item.event, queueManager)

        val notificationToggleView = v?.findViewById<ImageView>(R.id.notification_toggle_button)!!
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

        return true
    }
}