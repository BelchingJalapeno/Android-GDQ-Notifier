package com.belchingjalapeno.agdqschedulenotifier.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEventDatabase

class BootAlarmStarterReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, p1: Intent?) {
        val action = p1?.action
        if (action == null || action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        val database = NotificationEventDatabase.getDatabase(context)
        val notificationEventDao = database.notificationEventDao()

        notificationEventDao.deletePastEvents(System.currentTimeMillis())

        val events = notificationEventDao.getEarliestEvents(1)

        if (events.isEmpty()) {
            return
        }

        val timeWindow = getTimeWindow()
        val event = events[0]
        val alarmManagerNotifier = AlarmManagerNotifier(context, timeWindow)

        alarmManagerNotifier.setAlarm(event.id, event.speedRunEvent.startTime)
    }

    //todo actually get time window from somewhere instead of hardcoded value
    private fun getTimeWindow(): Long {
        return 5L * 60L * 1000L
    }
}