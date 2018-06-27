package com.belchingjalapeno.agdqschedulenotifier

import android.view.View

class BackgroundColorSetter {

    private val timeCalculator = TimeCalculator()

    fun setColor(v: View?, event: SpeedRunEvent, queueManager: WorkQueueManager) {
        if (timeCalculator.getTimeDiff(System.currentTimeMillis(), event.startTime) <= 0) {
            v?.setBackgroundColor(queueManager.oldEventColor)
        } else {
            v?.setBackgroundColor(queueManager.nonQueuedColor)
        }
    }

}