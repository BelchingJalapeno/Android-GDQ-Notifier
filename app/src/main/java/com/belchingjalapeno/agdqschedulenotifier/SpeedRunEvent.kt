package com.belchingjalapeno.agdqschedulenotifier

import androidx.work.Data
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.io.Reader

data class SpeedRunEvent(
        @SerializedName("time")
        val startTime: String,
        val game: String,
        val runners: String,
        @SerializedName("est_time")
        val runLength: String,
        val category: String,
        val casters: String,
        @SerializedName("setup_time")
        val setupTime: String
)

fun dataToEvent(data: Data): SpeedRunEvent {
    return Gson().fromJson(data.getString("data", ""), SpeedRunEvent::class.java)
}

fun eventToData(event: SpeedRunEvent): Data {
    return Data.Builder()
            .putString("data", Gson().toJson(event))
            .build()
}

fun getEvents(reader: Reader): Array<SpeedRunEvent> {
    val gson = GsonBuilder().create()
    return gson.fromJson<Array<SpeedRunEvent>>(reader, Array<SpeedRunEvent>::class.java)
}