package com.belchingjalapeno.agdqschedulenotifier

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Reader

data class SpeedRunEvent(
        @SerializedName("start_time")
        val startTime: Long,
        val game: String,
        val runners: String,
        @SerializedName("est_time")
        val estimatedTime: String,
        val category: String,
        val casters: String,
        @SerializedName("setup_time")
        val setupTime: String
)

class EventData(
        @SerializedName("twitch_url")
        val twitchUrl: String?,
        @SerializedName("donate_url")
        val donateUrl: String?,
        @SerializedName("events")
        val speedRunEvents: Array<SpeedRunEvent>
)

private val gson = Gson()

fun eventDataToJsonString(eventData: EventData): String {
    return gson.toJson(eventData)
}

fun getEvents(jsonString: String): Array<SpeedRunEvent> {
    return gson.fromJson<Array<SpeedRunEvent>>(jsonString, Array<SpeedRunEvent>::class.java)
}

fun getEventData(reader: Reader): EventData {
    return gson.fromJson<EventData>(reader, EventData::class.java)
}

fun getEventData(jsonString: String): EventData {
    return gson.fromJson<EventData>(jsonString, EventData::class.java)
}