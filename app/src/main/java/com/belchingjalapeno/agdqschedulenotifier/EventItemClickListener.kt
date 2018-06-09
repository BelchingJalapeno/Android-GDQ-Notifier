package com.belchingjalapeno.agdqschedulenotifier

import android.view.View
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.listeners.OnClickListener

class EventItemClickListener(private val queueManager: WorkQueueManager) : OnClickListener<EventItem> {
    private val timeCalculator = TimeCalculator()
    private val backgroundColorSetter = BackgroundColorSetter()

    override fun onClick(v: View?, adapter: IAdapter<EventItem>?, item: EventItem, position: Int): Boolean {
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

        return true
    }
}