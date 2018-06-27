package com.belchingjalapeno.agdqschedulenotifier.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * @param timeWindow duration in milliseconds before alarm exact time that it can go off
 */
class AlarmManagerNotifier(private val context: Context, private val timeWindow: Long) {

    companion object {
        const val REQUEST_CODE = 1
        const val BUNDLE_ID = "com.blechingjalapeno.agdqschedulenotifier.notification.AlarmManagerNotifier"
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * @param time time like System.currentTimeMillis()
     */
    fun setAlarm(id: Int, time: Long) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, createPendingIntent(id))
//        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, time - timeWindow, timeWindow, createPendingIntent(id))
    }

    fun cancelAlarm(id: Int) {
        alarmManager.cancel(createPendingIntent(id))
    }

    private fun createPendingIntent(id: Int): PendingIntent {
        return PendingIntent.getBroadcast(context, REQUEST_CODE, createIntent(id), PendingIntent.FLAG_ONE_SHOT)
    }

    private fun createIntent(id: Int): Intent {
        val intent = Intent(context, AlarmNotificationReceiver::class.java)
        intent.putExtra(BUNDLE_ID, id)
        return intent
    }
}