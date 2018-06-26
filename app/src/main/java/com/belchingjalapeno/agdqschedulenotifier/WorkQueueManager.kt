package com.belchingjalapeno.agdqschedulenotifier

import android.app.AlarmManager
import android.content.SharedPreferences
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit


class WorkQueueManager(private val pref: SharedPreferences,
                       val queuedColor: Int,
                       val nonQueuedColor: Int,
                       val oldEventColor: Int) {

    private val timeCalculator = TimeCalculator()

    fun isQueued(event: SpeedRunEvent): Boolean {
        if ((event.startTime) < System.currentTimeMillis()) {
            pref.edit()
                    .remove(event.hashCode().toString())
                    .apply()
        }
        return pref.contains(event.hashCode().toString())
    }

    fun removeFromQueue(event: SpeedRunEvent) {
        val uuidString = pref.getString(event.hashCode().toString(), "")
        WorkManager.getInstance().synchronous().cancelWorkByIdSync(UUID.fromString(uuidString))
        if (SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            val nextAlarm = Settings.System.getString(getContentResolver(),
                    Settings.System.NEXT_ALARM_FORMATTED)
        } else {
            val v: AlarmManager = InstrumentationRegistry.getContext().getSystemService(AlarmManager::class.java)
            v.nextAlarmClock
        }

        pref.edit()
                .remove(event.hashCode().toString())
                .apply()
    }

    fun addToQueue(event: SpeedRunEvent, millisecondsFromNow: Long) {
        if (millisecondsFromNow <= 0) {
            return
        }

        val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                // show notification 15 mins before start to give time to get ready / notification to show up
//                .setInitialDelay(millisecondsFromNow /*- (1000L * 60L * 15L)*/, TimeUnit.MILLISECONDS)
                .setInitialDelay(millisecondsFromNow, TimeUnit.MILLISECONDS)
                .setInputData(eventToData(event))
                .build()

        pref.edit()
                .putString(event.hashCode().toString(), workRequest.id.toString())
                .apply()

        WorkManager.getInstance().synchronous().enqueueSync(workRequest)
    }

    fun clearAll() {
        WorkManager.getInstance().synchronous().cancelAllWorkSync()
    }

    fun size(): Int {
        return -1
    }
}