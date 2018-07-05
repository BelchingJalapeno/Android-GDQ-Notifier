package com.belchingjalapeno.agdqschedulenotifier.notifications.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(NotificationEvent::class)], version = 1)
abstract class NotificationEventDatabase : RoomDatabase() {

    companion object {

        private var database: NotificationEventDatabase? = null

        fun getDatabase(context: Context): NotificationEventDatabase {
            if (database == null) {
                database = Room.databaseBuilder(context.applicationContext, NotificationEventDatabase::class.java, "Notification Database")
                        .build()
            }
            return database!!
        }

        /**
         * used for testing
         */
        fun setDatabase(database: NotificationEventDatabase){
            this.database = database
        }
    }

    abstract fun notificationEventDao(): NotificationEventDao

}