package com.belchingjalapeno.agdqschedulenotifier

import android.content.Context
import java.io.*

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

    private fun getEventsFile() = File(context.filesDir, "events.json")

    fun saveEvents(eventData: EventData) {
        val fileWriter = FileWriter(getEventsFile())
        fileWriter.write(eventDataToJsonString(eventData))
        fileWriter.close()
    }

    private fun getDefaultEventData(): EventData {
        val resource = context.resources.openRawResource(R.raw.events)
        val eventData = getEventData(BufferedReader(InputStreamReader(resource)))
        resource.close()
        return eventData
    }
}