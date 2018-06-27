package com.belchingjalapeno.agdqschedulenotifier

import android.content.Context
import com.belchingjalapeno.agdqschedulenotifier.notifications.AlarmManagerNotifier
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEvent
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEventDatabase
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.getEvent

class WorkQueueManager(context: Context) {

    private val database = NotificationEventDatabase.getDatabase(context)
    private val eventsDao = database.notificationEventDao()

    private val alarmManagerNotifier = AlarmManagerNotifier(context, 5L * 60L * 1000L)

    fun isQueued(event: SpeedRunEvent): Boolean {
        if ((event.startTime) < System.currentTimeMillis()) {
            removeEvent(event)
        }
        setNearestAlarm()
        return eventsDao.getEvent(event) != null
    }

    fun removeFromQueue(event: SpeedRunEvent) {
        removeEvent(event)

        setNearestAlarm()
    }

    fun addToQueue(event: SpeedRunEvent) {
        eventsDao.insert(NotificationEvent(0, event))

        setNearestAlarm()
    }

    fun clearAll() {
        val earliestEvents = eventsDao.getEarliestEvents(1)
        if (!earliestEvents.isEmpty()) {
            alarmManagerNotifier.cancelAlarm(earliestEvents[0].id)
        }

        eventsDao.getAll()
                .forEach {
                    eventsDao.delete(it)
                }
    }

    fun size(): Int {
        return eventsDao.getAll().size
    }

    private fun removeEvent(event: SpeedRunEvent) {
        val notificationEvent = eventsDao.getEvent(event)
        if (notificationEvent != null) {
            eventsDao.delete(notificationEvent)
        }
    }

    private fun setNearestAlarm() {
        eventsDao.deletePastEvents(System.currentTimeMillis())

        val earliestEvents = eventsDao.getEarliestEvents(1)
        if (earliestEvents.isNotEmpty()) {
            val notificationEvent = earliestEvents[0]
            alarmManagerNotifier.setAlarm(notificationEvent.id, notificationEvent.speedRunEvent.startTime)
        }
    }
}