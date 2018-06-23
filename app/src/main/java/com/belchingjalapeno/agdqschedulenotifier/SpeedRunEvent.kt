package com.belchingjalapeno.agdqschedulenotifier

import androidx.work.Data
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

fun dataToEvent(data: Data): SpeedRunEvent {
    return gson.fromJson(data.getString("data", ""), SpeedRunEvent::class.java)
}

fun eventToData(event: SpeedRunEvent): Data {
    return Data.Builder()
            .putString("data", Gson().toJson(event))
            .build()
}

fun eventDataToJsonString(eventData: EventData): String {
    return gson.toJson(eventData)
}

fun getEvents(reader: String): Array<SpeedRunEvent> {
    return gson.fromJson<Array<SpeedRunEvent>>(reader, Array<SpeedRunEvent>::class.java)
}

fun getEventData(reader: Reader): EventData {
    return gson.fromJson<EventData>(reader, EventData::class.java)
}

fun getEventData(reader: String): EventData {
    return gson.fromJson<EventData>(reader, EventData::class.java)
}