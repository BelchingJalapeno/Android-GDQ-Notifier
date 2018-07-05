package com.belchingjalapeno.agdqschedulenotifier.notifications.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.belchingjalapeno.agdqschedulenotifier.SpeedRunEvent


@Dao
interface NotificationEventDao {

    @Query("SELECT * FROM NotificationEvent ORDER BY startTime LIMIT :amount")
    fun getEarliestEvents(amount: Int): List<NotificationEvent>

    @Query("SELECT * FROM NotificationEvent WHERE startTime IS :startTime AND estimatedTime IS :estimatedTime AND setupTime IS :setupTime AND game IS :game AND category IS :category AND runners IS :runners AND casters IS :casters")
    fun getEvent(startTime: Long, estimatedTime: String, setupTime: String, game: String, category: String, runners: String, casters: String): NotificationEvent?

    @Query("SELECT * FROM NotificationEvent WHERE id IS :id")
    fun getEvent(id: Int): NotificationEvent?

    @Query("SELECT * FROM NotificationEvent")
    fun getAll(): List<NotificationEvent>

    @Insert
    fun insert(event: NotificationEvent): Long

    @Delete
    fun delete(event: NotificationEvent)

    @Query("DELETE FROM NotificationEvent WHERE startTime < :currentTimeMillis")
    fun deletePastEvents(currentTimeMillis: Long)

    @Query("SELECT * FROM NotificationEvent ORDER BY startTime LIMIT :amount")
    fun getEarliestEventsLiveData(amount: Int): LiveData<List<NotificationEvent>>

    @Query("SELECT * FROM NotificationEvent WHERE startTime IS :startTime AND estimatedTime IS :estimatedTime AND setupTime IS :setupTime AND game IS :game AND category IS :category AND runners IS :runners AND casters IS :casters")
    fun getEventLiveData(startTime: Long, estimatedTime: String, setupTime: String, game: String, category: String, runners: String, casters: String): LiveData<NotificationEvent?>

    @Query("SELECT * FROM NotificationEvent WHERE id IS :id")
    fun getEventLiveData(id: Int): LiveData<NotificationEvent?>

    @Query("SELECT * FROM NotificationEvent")
    fun getAllLiveData(): LiveData<List<NotificationEvent>>

}

fun NotificationEventDao.getEvent(speedRunEvent: SpeedRunEvent): NotificationEvent? {
    return getEvent(
            speedRunEvent.startTime,
            speedRunEvent.estimatedTime,
            speedRunEvent.setupTime,
            speedRunEvent.game,
            speedRunEvent.category,
            speedRunEvent.runners,
            speedRunEvent.casters
    )
}

fun NotificationEventDao.getEventLiveData(speedRunEvent: SpeedRunEvent): LiveData<NotificationEvent?> {
    return getEventLiveData(
            speedRunEvent.startTime,
            speedRunEvent.estimatedTime,
            speedRunEvent.setupTime,
            speedRunEvent.game,
            speedRunEvent.category,
            speedRunEvent.runners,
            speedRunEvent.casters
    )
}