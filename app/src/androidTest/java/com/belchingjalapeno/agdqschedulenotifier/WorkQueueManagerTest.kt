package com.belchingjalapeno.agdqschedulenotifier

import android.app.AlarmManager
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkQueueManagerTest {

    private lateinit var workQueueManager: WorkQueueManager
    private val testEvent1 = SpeedRunEvent(Long.MAX_VALUE - 1, "name1", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")
    private val testEvent2 = SpeedRunEvent(Long.MAX_VALUE - 2, "name2", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")
    private val testEvent3 = SpeedRunEvent(Long.MAX_VALUE - 3, "name3", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")

    @Before
    fun setUp() {
        workQueueManager = WorkQueueManager(InstrumentationRegistry.getTargetContext(), 0, 1, 2)
    }

    @After
    fun tearDown() {
        workQueueManager.clearAll()
    }

    @Test
    fun addToQueue() {
        workQueueManager.addToQueue(testEvent1)

        assertThat(workQueueManager.size(), equalTo(1))

        workQueueManager.addToQueue(testEvent2)
        workQueueManager.addToQueue(testEvent3)

        assertThat(workQueueManager.size(), equalTo(3))
    }

    @Test
    fun isQueued() {
        workQueueManager.addToQueue(testEvent1)

        assertThat(workQueueManager.isQueued(testEvent1), equalTo(true))
    }

    @Test
    fun removeFromQueue() {
        workQueueManager.addToQueue(testEvent1)

        assertThat(workQueueManager.isQueued(testEvent1), equalTo(true))

        workQueueManager.removeFromQueue(testEvent1)
        assertThat(workQueueManager.isQueued(testEvent1), equalTo(false))

        val v: AlarmManager = InstrumentationRegistry.getContext().getSystemService(AlarmManager::class.java)
    }

    @Test
    fun clearAll() {
        workQueueManager.addToQueue(testEvent1)
        workQueueManager.addToQueue(testEvent2)
        workQueueManager.addToQueue(testEvent3)

        assertThat(workQueueManager.size(), equalTo(3))

        workQueueManager.clearAll()

        assertThat(workQueueManager.size(), equalTo(0))
    }
}