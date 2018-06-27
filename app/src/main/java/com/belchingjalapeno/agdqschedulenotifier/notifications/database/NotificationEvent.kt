package com.belchingjalapeno.agdqschedulenotifier.notifications.database

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.belchingjalapeno.agdqschedulenotifier.SpeedRunEvent

@Entity
data class NotificationEvent(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @Embedded val speedRunEvent: SpeedRunEvent
)