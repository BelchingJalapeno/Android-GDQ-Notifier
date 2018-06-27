package com.belchingjalapeno.agdqschedulenotifier.notifications

import com.belchingjalapeno.agdqschedulenotifier.SpeedRunEvent
import com.belchingjalapeno.agdqschedulenotifier.TimeCalculator

class EventToNotificationTextFormatter {

    private val timeUtils = TimeCalculator()

    fun getContentTitle(event: SpeedRunEvent): String {
        return "${event.game}(${event.category}) starting at ${timeUtils.getTimeFromMilliseconds(event.startTime)}"
    }

    fun getContentText(event: SpeedRunEvent): String {
        val estimatedMillis = timeUtils.fromStringExpectedLengthToLong(event.estimatedTime)
        val formattedEstimatedTime = timeUtils.getFormattedTime(estimatedMillis, true, true, true)
        return "Length $formattedEstimatedTime"
    }

    fun getBigText(event: SpeedRunEvent, nextEvent: SpeedRunEvent?, nextNextEvent: SpeedRunEvent?): String {
        var bigText = getContentText(event)
        if (nextEvent != null) {
            bigText += "\n${nextEvent.game} ${timeUtils.getRelativeTimeFromMilliseconds(nextEvent.startTime)}"
        }
        if (nextNextEvent != null) {
            bigText += "\n${nextNextEvent.game} ${timeUtils.getRelativeTimeFromMilliseconds(nextNextEvent.startTime)}"
        }
        return bigText
    }
}