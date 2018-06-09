package com.belchingjalapeno.agdqschedulenotifier

import android.view.View

class BackgroundColorSetter {

    private val timeCalculator = TimeCalculator()

    fun setColor(v: View?, event: SpeedRunEvent, queueManager: WorkQueueManager) {
        if (timeCalculator.getTimeDiff(System.currentTimeMillis(), timeCalculator.fromStringStartTimeToLong(event.startTime)) <= 0) {
            v?.setBackgroundColor(queueManager.oldEventColor)
        } else if (queueManager.isQueued(event)) {
            v?.setBackgroundColor(queueManager.queuedColor)
        } else {
            v?.setBackgroundColor(queueManager.nonQueuedColor)
        }
    }

}