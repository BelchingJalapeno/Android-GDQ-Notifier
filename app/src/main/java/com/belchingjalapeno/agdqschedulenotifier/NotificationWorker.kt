package com.belchingjalapeno.agdqschedulenotifier

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import androidx.work.Worker

class NotificationWorker : Worker() {

    private val CHANNEL_ID = "Speed Run Channel"

    private val timeCalculator = TimeCalculator()

    override fun doWork(): Result {
        createNotificationChannel()

        val event = dataToEvent(inputData)
        val notification = createNotification(createTwitchIntent(), event)
        showNotification(notification, event.hashCode())

        return Result.SUCCESS
    }

    private fun showNotification(notification: Notification, eventId: Int) {
        val notificationManager = NotificationManagerCompat.from(this.applicationContext)

        notificationManager.notify(eventId, notification)
    }

    private fun createNotification(pendingIntent: PendingIntent, event: SpeedRunEvent): Notification {
        val formattedEstimatedTime = timeCalculator.getFormattedTime(timeCalculator.fromStringExpectedLengthToLong(event.runLength), true, true, true)
        val contentText = "Length $formattedEstimatedTime\n"
        val mBuilder = NotificationCompat.Builder(this.applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.glitch_icon_white_128px)
                .setContentTitle("${event.game}(${event.category}) starting soon")
                .setContentText(contentText)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(contentText +
                                "Runners ${event.runners}\n" +
                                "Casters ${event.casters}"))
                // Set the intent that will fire when the user taps the notification
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        return mBuilder.build()
    }

    private fun createNotificationIntent(): PendingIntent {
        val intent = Intent(this.applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(this.applicationContext, 0, intent, 0)
    }

    private fun createTwitchIntent(): PendingIntent {
        return TwitchIntentBuilder.getTwitchPendingIntent(applicationContext)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_name)
//            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "name todo", importance)
            channel.description = "channel description todo"
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = this.applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}