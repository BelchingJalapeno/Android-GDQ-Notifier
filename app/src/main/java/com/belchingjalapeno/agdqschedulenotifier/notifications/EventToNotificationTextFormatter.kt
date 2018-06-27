package com.belchingjalapeno.agdqschedulenotifier.notifications

import com.belchingjalapeno.agdqschedulenotifier.SpeedRunEvent
import com.belchingjalapeno.agdqschedulenotifier.TimeFormatter

class EventToNotificationTextFormatter {

    private val timeUtils = TimeFormatter()

    fun getContentTitle(currentEvent: SpeedRunEvent): String {
        return "${currentEvent.game}(${currentEvent.category}) starting at ${timeUtils.getTimeFromMilliseconds(currentEvent.startTime)}"
    }

    fun getContentText(nextEvent: SpeedRunEvent?): String {
        return if (nextEvent != null) {
            "${nextEvent.game} ${timeUtils.getRelativeTimeFromMilliseconds(nextEvent.startTime)}"
        } else {
            "Click to watch"
        }
    }

    fun getBigText(nextEvent: SpeedRunEvent?, nextNextEvent: SpeedRunEvent?, nextNextNextEvent: SpeedRunEvent?): String {
        var bigText = getContentText(nextEvent)
        if (nextNextEvent != null) {
            bigText += "\n${nextNextEvent.game} ${timeUtils.getRelativeTimeFromMilliseconds(nextNextEvent.startTime)}"
        }
        if (nextNextNextEvent != null) {
            bigText += "\n${nextNextNextEvent.game} ${timeUtils.getRelativeTimeFromMilliseconds(nextNextNextEvent.startTime)}"
        }
        return bigText
    }
}