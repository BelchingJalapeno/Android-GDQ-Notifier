package com.belchingjalapeno.agdqschedulenotifier.notifications

import android.content.Context
import com.belchingjalapeno.agdqschedulenotifier.SpeedRunEvent
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEvent
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEventDatabase
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.getEvent
import java.util.concurrent.Executors

class NotificationQueue(context: Context) {

    private val database = NotificationEventDatabase.getDatabase(context.applicationContext)
    private val eventsDao = database.notificationEventDao()

    private val alarmManagerNotifier = AlarmManagerNotifier(context.applicationContext)

    private val cache = NotificationCache.getNotificationCache(context)

    private val backgroundExecutor = Executors.newSingleThreadExecutor()

    init {
        backgroundExecutor.submit { cache.update() }
    }

    fun isQueued(event: SpeedRunEvent): Boolean {
        if ((event.startTime) < System.currentTimeMillis()) {
            removeEvent(event)
        }
        setNearestAlarm()
        return cache.isQueued(event)
    }

    fun removeFromQueue(event: SpeedRunEvent) {
        removeEvent(event)

        setNearestAlarm()
    }

    fun addToQueue(event: SpeedRunEvent) {
        backgroundExecutor.submit {
            eventsDao.insert(NotificationEvent(0, event))

            setNearestAlarm()

            cache.update()
        }
    }

    fun clearAll() {
        backgroundExecutor.submit {
            val earliestEvents = eventsDao.getEarliestEvents(1)
            if (!earliestEvents.isEmpty()) {
                alarmManagerNotifier.cancelAlarm(earliestEvents[0].id)
            }

            eventsDao.getAll()
                    .forEach {
                        eventsDao.delete(it)
                    }

            cache.update()
        }
    }

    fun size(): Int {
        return cache.size
    }

    private fun removeEvent(event: SpeedRunEvent) {
        backgroundExecutor.submit {
            val notificationEvent = eventsDao.getEvent(event)
            if (notificationEvent != null) {
                eventsDao.delete(notificationEvent)
            }

            cache.update()
        }
    }

    private fun setNearestAlarm() {
        backgroundExecutor.submit {
            eventsDao.deletePastEvents(System.currentTimeMillis())

            val earliestEvents = eventsDao.getEarliestEvents(1)
            if (earliestEvents.isNotEmpty()) {
                val notificationEvent = earliestEvents[0]
                alarmManagerNotifier.setAlarm(notificationEvent.id, notificationEvent.speedRunEvent.startTime)
            }

            cache.update()
        }
    }
}