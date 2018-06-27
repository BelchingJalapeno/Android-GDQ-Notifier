package com.belchingjalapeno.agdqschedulenotifier.notifications.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(NotificationEvent::class)], version = 1)
abstract class NotificationEventDatabase : RoomDatabase() {

    companion object {

        private var database: NotificationEventDatabase? = null

        fun getDatabase(context: Context): NotificationEventDatabase {
            if (database == null) {
                database = Room.databaseBuilder(context.applicationContext, NotificationEventDatabase::class.java, "Notification Database")
                        .allowMainThreadQueries()//todo remove this after refactoring
                        .build()
            }
            return database!!
        }
    }

    abstract fun notificationEventDao(): NotificationEventDao

}