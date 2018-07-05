package com.belchingjalapeno.agdqschedulenotifier.notifications.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import com.belchingjalapeno.agdqschedulenotifier.SpeedRunEvent
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*

class NotificationEventDatabaseTest {

    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    companion object {

        private lateinit var database: NotificationEventDatabase
        private lateinit var notificationEventDao: NotificationEventDao
        private val testNotificationsList = listOf(
                NotificationEvent(speedRunEvent = SpeedRunEvent(1, "game1", "runner1", "1:11:11", "any", "caster1", "1:11:11")),
                NotificationEvent(speedRunEvent = SpeedRunEvent(2, "game2", "runner2", "2:22:22", "any", "caster2", "2:22:22")),
                NotificationEvent(speedRunEvent = SpeedRunEvent(3, "game3", "runner3", "3:33:33", "any", "caster3", "3:33:33")),
                NotificationEvent(speedRunEvent = SpeedRunEvent(4, "game4", "runner4", "4:44:44", "any", "caster4", "4:44:44")),
                NotificationEvent(speedRunEvent = SpeedRunEvent(5, "game5", "runner5", "5:55:55", "any", "caster5", "5:55:55"))
        )
        private val testExtraSpeedRunEvent = SpeedRunEvent(0, "game-0", "runner-0", "0:00:00", "any", "caster0", "0:00:00")

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(), NotificationEventDatabase::class.java)
                    .allowMainThreadQueries()
                    .build()
            notificationEventDao = database.notificationEventDao()
        }

        @JvmStatic
        @AfterClass
        fun tearDownClass() {
            database.close()
        }
    }

    @Before
    fun setUp() {
        database.clearAllTables()

        testNotificationsList.forEach {
            database.notificationEventDao().insert(it)
        }
    }


    @Test
    fun getEarliestEvents() {
        val actualEvents = notificationEventDao.getEarliestEventsLiveData(3).valueNow()?.map { it.speedRunEvent }
        val expectedEvents = testNotificationsList.take(3).map { it.speedRunEvent }
        assertThat(actualEvents, equalTo(expectedEvents))
    }

    @Test
    fun getEvent() {
        val expectedEvent = testNotificationsList[2].speedRunEvent
        val actualEvent = notificationEventDao.getEventLiveData(expectedEvent).valueNow()?.speedRunEvent

        assertThat(actualEvent, equalTo(expectedEvent))
    }

    @Test
    fun getEventById() {
        val id = notificationEventDao.insert(NotificationEvent(speedRunEvent = testExtraSpeedRunEvent))

        val actualEvent = notificationEventDao.getEventLiveData(id.toInt()).valueNow()
        val expectedEvent = NotificationEvent(id.toInt(), testExtraSpeedRunEvent)

        assertThat(actualEvent, equalTo(expectedEvent))
    }

    @Test
    fun getAll() {
        val expectedEvents = testNotificationsList.map { it.speedRunEvent }
        val actualEvents = notificationEventDao.getAllLiveData().valueNow()?.map { it.speedRunEvent }

        assertThat(actualEvents, equalTo(expectedEvents))
    }

    @Test
    fun insert() {
        val id = notificationEventDao.insert(NotificationEvent(speedRunEvent = testExtraSpeedRunEvent))

        val actualEvent = notificationEventDao.getEventLiveData(testExtraSpeedRunEvent).valueNow()
        val expectedEvent = NotificationEvent(id.toInt(), testExtraSpeedRunEvent)

        assertThat(actualEvent, equalTo(expectedEvent))
    }

    @Test
    fun delete() {
        val deletedNotification = notificationEventDao.getEventLiveData(testNotificationsList[2].speedRunEvent).valueNow()!!
        notificationEventDao.delete(deletedNotification)

        val actualEvent = notificationEventDao.getEventLiveData(deletedNotification.speedRunEvent).valueNow()
        val expectedEvent = nullValue()

        assertThat(actualEvent, expectedEvent)
    }

    @Test
    fun deletePastEvents() {
        val currentTime = 2L

        notificationEventDao.deletePastEvents(currentTime)

        val actualEvents = notificationEventDao.getAllLiveData().valueNow()?.map { it.speedRunEvent }
        val expectedEvents = testNotificationsList.filter { it.speedRunEvent.startTime >= currentTime }.map { it.speedRunEvent }

        assertThat(actualEvents, equalTo(expectedEvents))
    }
}

fun <T> LiveData<T>.valueNow(): T? {
    var value: T? = null
    this.observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            value = t
            removeObserver(this)
        }
    })
    return value
}