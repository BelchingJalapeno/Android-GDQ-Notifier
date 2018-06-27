package com.belchingjalapeno.agdqschedulenotifier.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AlarmManagerNotifier(private val context: Context) {

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
    }

    fun cancelAlarm(id: Int) {
        alarmManager.cancel(createPendingIntent(id))
    }

    private fun createPendingIntent(id: Int): PendingIntent {
        return PendingIntent.getBroadcast(context, REQUEST_CODE, createIntent(id), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createIntent(id: Int): Intent {
        val intent = Intent(context, AlarmNotificationReceiver::class.java)
        intent.putExtra(BUNDLE_ID, id)
        return intent
    }
}