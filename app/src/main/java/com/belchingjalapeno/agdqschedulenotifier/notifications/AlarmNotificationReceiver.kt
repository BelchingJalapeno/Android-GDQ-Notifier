package com.belchingjalapeno.agdqschedulenotifier.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEvent
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEventDao
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEventDatabase

/**
 * Creates a notification after being called from the AlarmManager
 */
class AlarmNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventId = getId(intent.extras)

        val database = NotificationEventDatabase.getDatabase(context)
        val eventDao = database.notificationEventDao()
        val notificationCreator = NotificationCreator(context)
        val alarmManagerNotifier = AlarmManagerNotifier(context)

        val pendingIntent = goAsync()

        Thread({

            val currentEvent = eventDao.getEvent(eventId)
            if (currentEvent == null) {
                eventDao.deletePastEvents(System.currentTimeMillis())
                setNextAlarm(eventDao, alarmManagerNotifier)

                pendingIntent.finish()

                return@Thread
            }
            eventDao.delete(currentEvent)
            eventDao.deletePastEvents(System.currentTimeMillis())

            val events = eventDao.getEarliestEvents(3)

            val nextEvent = getEventOrNull(events, 0)
            val nextNextEvent = getEventOrNull(events, 1)
            val nextNextNextEvent = getEventOrNull(events, 2)
            notificationCreator.showNotification(currentEvent, nextEvent, nextNextEvent, nextNextNextEvent)

            //set alarm for next event
            setNextAlarm(eventDao, alarmManagerNotifier)

            pendingIntent.finish()
        }, "AlarmNotificationThread"
        ).start()
    }

    private fun setNextAlarm(eventDao: NotificationEventDao, alarmManagerNotifier: AlarmManagerNotifier) {
        eventDao.deletePastEvents(System.currentTimeMillis())
        val earliestEvents = eventDao.getEarliestEvents(1)
        if (earliestEvents.isNotEmpty()) {
            val event = earliestEvents[0]
            alarmManagerNotifier.setAlarm(event.id, event.speedRunEvent.startTime)
        }
    }

    private fun getId(bundle: Bundle): Int {
        return bundle.getInt(AlarmManagerNotifier.BUNDLE_ID)
    }

    private fun getEventOrNull(events: List<NotificationEvent>, index: Int): NotificationEvent? {
        return if (events.size <= index) {
            null
        } else {
            events[index]
        }
    }
}
