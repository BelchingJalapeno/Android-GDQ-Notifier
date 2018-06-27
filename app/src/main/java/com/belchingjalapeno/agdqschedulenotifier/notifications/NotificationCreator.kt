package com.belchingjalapeno.agdqschedulenotifier.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.belchingjalapeno.agdqschedulenotifier.ExternalIntentsBuilder
import com.belchingjalapeno.agdqschedulenotifier.MainActivity
import com.belchingjalapeno.agdqschedulenotifier.R
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEvent

class NotificationCreator(private val context: Context) {

    private val CHANNEL_ID = "Speed Run Channel"

    private val notificationTextFormatter = EventToNotificationTextFormatter()

    init {
        createNotificationChannel()
    }

    fun showNotification(event: NotificationEvent, nextEvent: NotificationEvent?, nextNextEvent: NotificationEvent?, nextNextNextEvent: NotificationEvent?) {
        val contentTitle = notificationTextFormatter.getContentTitle(event.speedRunEvent)
        val contentText = notificationTextFormatter.getContentText(nextEvent?.speedRunEvent)
        val bigText = notificationTextFormatter.getBigText(nextEvent?.speedRunEvent, nextNextEvent?.speedRunEvent, nextNextNextEvent?.speedRunEvent)
        val notification = createNotification(createTwitchIntent(), contentTitle, contentText, bigText)
        showNotification(notification, event.id)
    }

    private fun showNotification(notification: Notification, eventId: Int) {
        val notificationManager = NotificationManagerCompat.from(context.applicationContext)

        notificationManager.notify(eventId, notification)
    }

    private fun createNotification(pendingIntent: PendingIntent,
                                   contentTitle: String,
                                   contentText: String,
                                   bigText: String): Notification {

        val mBuilder = NotificationCompat.Builder(context.applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.glitch_icon_white_128px)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(bigText))
                // Set the intent that will fire when the user taps the notification
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        return mBuilder.build()
    }

    private fun createNotificationIntent(): PendingIntent {
        val intent = Intent(context.applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(context.applicationContext, 0, intent, 0)
    }

    private fun createTwitchIntent(): PendingIntent {
        val sharedPreferences = context.applicationContext.getSharedPreferences("enqueued", Context.MODE_PRIVATE)
        return ExternalIntentsBuilder.getTwitchPendingIntent(context.applicationContext, sharedPreferences)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val description = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
