package com.belchingjalapeno.agdqschedulenotifier

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.belchingjalapeno.agdqschedulenotifier.notifications.NotificationQueue
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.NotificationEventDatabase
import com.belchingjalapeno.agdqschedulenotifier.notifications.database.valueNow
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationQueueTest {

    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    private lateinit var notificationQueue: NotificationQueue
    private val testEvent1 = SpeedRunEvent(Long.MAX_VALUE - 1, "name1", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")
    private val testEvent2 = SpeedRunEvent(Long.MAX_VALUE - 2, "name2", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")
    private val testEvent3 = SpeedRunEvent(Long.MAX_VALUE - 3, "name3", "Runner, runner2", "3:45:67", "ANY", "caster1, caster2", "0:30:00")

    @Before
    fun setUp() {
        val database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(), NotificationEventDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        NotificationEventDatabase.setDatabase(database)
        notificationQueue = NotificationQueue(InstrumentationRegistry.getTargetContext())
    }

    @After
    fun tearDown() {
        notificationQueue.clearAll()
        waitForQueueTasksToFinish()
    }

    @Test
    fun addToQueue() {
        notificationQueue.addToQueue(testEvent1)

        waitForQueueTasksToFinish()
        assertThat(notificationQueue.size().valueNow(), equalTo(1))

        notificationQueue.addToQueue(testEvent2)
        notificationQueue.addToQueue(testEvent3)

        waitForQueueTasksToFinish()
        assertThat(notificationQueue.size().valueNow(), equalTo(3))
    }

    @Test
    fun isQueued() {
        notificationQueue.addToQueue(testEvent1)

        waitForQueueTasksToFinish()
        assertThat(notificationQueue.isQueued(testEvent1).valueNow(), equalTo(true))
    }

    @Test
    fun removeFromQueue() {
        notificationQueue.addToQueue(testEvent1)

        waitForQueueTasksToFinish()
        assertThat(notificationQueue.isQueued(testEvent1).valueNow(), equalTo(true))

        notificationQueue.removeFromQueue(testEvent1)

        waitForQueueTasksToFinish()
        assertThat(notificationQueue.isQueued(testEvent1).valueNow(), equalTo(false))
    }

    @Test
    fun clearAll() {
        notificationQueue.addToQueue(testEvent1)
        notificationQueue.addToQueue(testEvent2)
        notificationQueue.addToQueue(testEvent3)

        waitForQueueTasksToFinish()
        assertThat(notificationQueue.size().valueNow(), equalTo(3))

        notificationQueue.clearAll()

        waitForQueueTasksToFinish()
        assertThat(notificationQueue.size().valueNow(), equalTo(0))
    }

    private fun waitForQueueTasksToFinish() {
        NotificationQueue.backgroundExecutor.submit {}.get()
    }
}