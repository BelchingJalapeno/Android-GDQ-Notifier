package com.belchingjalapeno.agdqschedulenotifier.notifications.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.belchingjalapeno.agdqschedulenotifier.SpeedRunEvent

@Entity
data class NotificationEvent(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @Embedded val speedRunEvent: SpeedRunEvent
)