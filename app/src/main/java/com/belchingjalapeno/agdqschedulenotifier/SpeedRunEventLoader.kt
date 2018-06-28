package com.belchingjalapeno.agdqschedulenotifier

import android.content.Context
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class SpeedRunEventLoader(private val context: Context) {

    fun getEventData(): EventData {
        val eventsFile = getEventsFile()
        return if (eventsFile.exists()) {
            val fileReader = FileReader(eventsFile)
            val eventData = getEventData(fileReader)
            fileReader.close()
            eventData
        } else {
            val defaultEventData = getDefaultEventData()
            saveEvents(defaultEventData)
            defaultEventData
        }
    }

    fun getEvents(): Array<SpeedRunEvent> {
        return getEventData().speedRunEvents
    }

    fun saveEvents(eventData: EventData) {
        val fileWriter = FileWriter(getEventsFile())
        fileWriter.write(eventDataToJsonString(eventData))
        fileWriter.close()
    }

    fun getEventsByDay(events: Array<SpeedRunEvent>): Array<Array<SpeedRunEvent>> {
        val simpleDateFormat = SimpleDateFormat.getDateInstance()
        val date = Date()

        events.sortBy { it.startTime }

        return events.groupBy(
                {
                    date.time = it.startTime
                    simpleDateFormat.format(date)
                }
        )
                .values
                .map {
                    it.toTypedArray()
                }
                .toTypedArray()
    }

    private fun getEventsFile() = File(context.filesDir, "events.json")

    private fun getDefaultEventData(): EventData {
        val resource = context.resources.openRawResource(R.raw.events)
        val eventData = getEventData(BufferedReader(InputStreamReader(resource)))
        resource.close()
        return eventData
    }
}