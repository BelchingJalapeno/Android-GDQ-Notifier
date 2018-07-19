package com.belchingjalapeno.agdqschedulenotifier.notifications

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.belchingjalapeno.agdqschedulenotifier.SpeedRunEvent
import com.belchingjalapeno.agdqschedulenotifier.TimeFormatter
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEvent
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEventDatabase
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.getEvent
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.getEventLiveData
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class NotificationQueue(context: Context) {

    private val database = NotificationEventDatabase.getDatabase(context.applicationContext)
    private val eventsDao = database.notificationEventDao()

    private val alarmManagerNotifier = AlarmManagerNotifier(context.applicationContext)

    companion object {
        val backgroundExecutor = ThreadPoolExecutor(0, 1, 10L, TimeUnit.SECONDS, LinkedBlockingQueue())
    }

    fun isQueued(event: SpeedRunEvent): LiveData<Boolean> {
        if ((event.startTime) < TimeFormatter.getCurrentTime()) {
            removeEvent(event)
        }
        setNearestAlarm()
        return Transformations.map(database.notificationEventDao().getEventLiveData(event)) { it != null }
    }

    fun removeFromQueue(event: SpeedRunEvent) {
        removeEvent(event)

        setNearestAlarm()
    }

    fun addToQueue(event: SpeedRunEvent) {
        backgroundExecutor.submit {
            eventsDao.insert(NotificationEvent(0, event))

            setNearestAlarm()
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
        }
    }

    fun size(): LiveData<Int> {
        return Transformations.map(database.notificationEventDao().getAllLiveData()) { it.size }
    }

    private fun removeEvent(event: SpeedRunEvent) {
        backgroundExecutor.submit {
            val notificationEvent = eventsDao.getEvent(event)
            if (notificationEvent != null) {
                eventsDao.delete(notificationEvent)
            }
        }
    }

    private fun setNearestAlarm() {
        backgroundExecutor.submit {
            eventsDao.deletePastEvents(TimeFormatter.getCurrentTime())

            val earliestEvents = eventsDao.getEarliestEvents(1)
            if (earliestEvents.isNotEmpty()) {
                val notificationEvent = earliestEvents[0]
                alarmManagerNotifier.setAlarm(notificationEvent.id, notificationEvent.speedRunEvent.startTime)
            }
        }
    }

    fun toggleNotification(event: SpeedRunEvent) {
        val liveData = isQueued(event)
        liveData.observeForever(object : Observer<Boolean?> {
            override fun onChanged(value: Boolean?) {
                val isQueued = value ?: false
                if (isQueued) {
                    removeEvent(event)
                } else {
                    addToQueue(event)
                }
                liveData.removeObserver(this)
            }
        })
    }

    fun isQueuedNow(event: SpeedRunEvent): Boolean {
        //wait until all current tasks are finished in executor
        backgroundExecutor.submit {}.get()
        return database.notificationEventDao().getEvent(event) != null
    }
}