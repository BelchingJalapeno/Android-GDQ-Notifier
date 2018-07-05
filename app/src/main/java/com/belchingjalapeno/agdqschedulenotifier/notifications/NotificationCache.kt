package com.belchingjalapeno.agdqschedulenotifier.notifications

import android.content.Context
import com.belchingjalapeno.agdqschedulenotifier.SpeedRunEvent
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEvent
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEventDatabase

class NotificationCache(private val database: NotificationEventDatabase) {

    private val activeNotificationsList = mutableListOf<NotificationEvent>()
    val size: Int
        get() = activeNotificationsList.size

    fun isQueued(event: SpeedRunEvent): Boolean {
        return activeNotificationsList.find { it.speedRunEvent == event } != null
    }

    fun update() {
        val allNotifications = database.notificationEventDao().getAll()
        activeNotificationsList.clear()
        activeNotificationsList.addAll(allNotifications)
    }

    companion object {
        private var cache: NotificationCache? = null

        fun getNotificationCache(context: Context): NotificationCache {
            if (cache == null) {
                val database = NotificationEventDatabase.getDatabase(context)
                cache = NotificationCache(database)
            }
            return cache!!
        }
    }

}