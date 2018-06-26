package com.belchingjalapeno.agdqschedulenotifier

import android.app.Activity
import android.app.AlarmManager
import android.support.test.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.test.WorkManagerTestInitHelper
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class WorkQueueManagerTest {

    private lateinit var workQueueManager: WorkQueueManager
    private val testEvent1 = SpeedRunEvent(1, "name1", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")
    private val testEvent2 = SpeedRunEvent(2, "name2", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")
    private val testEvent3 = SpeedRunEvent(3, "name3", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")

    @Before
    fun setUp() {
        workQueueManager = WorkQueueManager(InstrumentationRegistry.getTargetContext().getSharedPreferences("TEST", Activity.MODE_PRIVATE), 0, 1, 2)
        WorkManagerTestInitHelper.initializeTestWorkManager(InstrumentationRegistry.getTargetContext(), Configuration.Builder().setExecutor { it.run() }.build())
    }

    @After
    fun tearDown() {
        workQueueManager.clearAll()
    }

    @Test
    fun addToQueue() {
        workQueueManager.addToQueue(testEvent1, 10000)

        assertThat(workQueueManager.size(), equalTo(1))

        workQueueManager.addToQueue(testEvent2, 100)
        workQueueManager.addToQueue(testEvent3, 1000)

        assertThat(workQueueManager.size(), equalTo(3))
    }

    @Test
    fun isQueued() {
        workQueueManager.addToQueue(testEvent1, 100000)

        assertThat(workQueueManager.isQueued(testEvent1), equalTo(true))
    }

    @Test
    fun removeFromQueue() {
        workQueueManager.addToQueue(testEvent1, 100000)

        assertThat(workQueueManager.isQueued(testEvent1), equalTo(true))

        workQueueManager.removeFromQueue(testEvent1)
        assertThat(workQueueManager.isQueued(testEvent1), equalTo(false))
    }

    @Test
    fun clearAll() {
        workQueueManager.addToQueue(testEvent1, 10000)
        workQueueManager.addToQueue(testEvent2, 100)
        workQueueManager.addToQueue(testEvent3, 1000)

        assertThat(workQueueManager.size(), equalTo(3))

        workQueueManager.clearAll()

        assertThat(workQueueManager.size(), equalTo(0))
    }
}