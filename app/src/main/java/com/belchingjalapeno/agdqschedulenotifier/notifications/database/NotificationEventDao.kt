package com.belchingjalapeno.agdqschedulenotifier.notifications.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.belchingjalapeno.agdqschedulenotifier.SpeedRunEvent


@Dao
abstract interface NotificationEventDao {

    @Query("SELECT * FROM NotificationEvent ORDER BY startTime LIMIT :amount")
    abstract fun getEarliestEvents(amount: Int): List<NotificationEvent>

    @Query("SELECT * FROM NotificationEvent WHERE startTime IS :startTime AND estimatedTime IS :estimatedTime AND setupTime IS :setupTime AND game IS :game AND category IS :category AND runners IS :runners AND casters IS :casters")
    abstract fun getEvent(startTime: Long, estimatedTime: String, setupTime: String, game: String, category: String, runners: String, casters: String): NotificationEvent?

    @Query("SELECT * FROM NotificationEvent WHERE id IS :id")
    fun getEvent(id: Int): NotificationEvent?

    @Query("SELECT * FROM NotificationEvent")
    abstract fun getAll(): List<NotificationEvent>

    @Insert
    abstract fun insert(event: NotificationEvent): Long

    @Delete
    abstract fun delete(event: NotificationEvent)

    @Query("DELETE FROM NotificationEvent WHERE startTime <= :currentTimeMillis")
    abstract fun deletePastEvents(currentTimeMillis: Long)


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
