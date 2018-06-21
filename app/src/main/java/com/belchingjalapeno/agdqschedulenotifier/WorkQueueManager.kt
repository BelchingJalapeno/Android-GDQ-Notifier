package com.belchingjalapeno.agdqschedulenotifier

import android.content.SharedPreferences
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
        if (timeCalculator.fromStringStartTimeToLong(event.startTime) < System.currentTimeMillis()) {
            pref.edit()
                    .remove(event.hashCode().toString())
                    .apply()
        }
        return pref.contains(event.hashCode().toString())
    }

    fun removeFromQueue(event: SpeedRunEvent) {
        val uuidString = pref.getString(event.hashCode().toString(), "")
        WorkManager.getInstance().cancelWorkById(UUID.fromString(uuidString))

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

        WorkManager.getInstance().enqueue(workRequest)
    }

    fun clearAll() {
        WorkManager.getInstance().cancelAllWork()
    }
}