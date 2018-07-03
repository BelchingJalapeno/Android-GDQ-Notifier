package com.belchingjalapeno.agdqschedulenotifier

import android.app.AlarmManager
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.belchingjalapeno.agdqschedulenotifier.notifications.NotificationQueue
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationQueueTest {

    private lateinit var notificationQueue: NotificationQueue
    private val testEvent1 = SpeedRunEvent(Long.MAX_VALUE - 1, "name1", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")
    private val testEvent2 = SpeedRunEvent(Long.MAX_VALUE - 2, "name2", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")
    private val testEvent3 = SpeedRunEvent(Long.MAX_VALUE - 3, "name3", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")

    @Before
    fun setUp() {
        notificationQueue = NotificationQueue(InstrumentationRegistry.getTargetContext())
    }

    @After
    fun tearDown() {
        notificationQueue.clearAll()
    }

    @Test
    fun addToQueue() {
        notificationQueue.addToQueue(testEvent1)

        assertThat(notificationQueue.size(), equalTo(1))

        notificationQueue.addToQueue(testEvent2)
        notificationQueue.addToQueue(testEvent3)

        assertThat(notificationQueue.size(), equalTo(3))
    }

    @Test
    fun isQueued() {
        notificationQueue.addToQueue(testEvent1)

        assertThat(notificationQueue.isQueued(testEvent1), equalTo(true))
    }

    @Test
    fun removeFromQueue() {
        notificationQueue.addToQueue(testEvent1)

        assertThat(notificationQueue.isQueued(testEvent1), equalTo(true))

        notificationQueue.removeFromQueue(testEvent1)
        assertThat(notificationQueue.isQueued(testEvent1), equalTo(false))
    }

    @Test
    fun clearAll() {
        notificationQueue.addToQueue(testEvent1)
        notificationQueue.addToQueue(testEvent2)
        notificationQueue.addToQueue(testEvent3)

        assertThat(notificationQueue.size(), equalTo(3))

        notificationQueue.clearAll()

        assertThat(notificationQueue.size(), equalTo(0))
    }
}