package com.belchingjalapeno.agdqschedulenotifier

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Time related calculations and formatting
 */
class TimeFormatter {

    fun fromStringExpectedLengthToLong(runLength: String): Long {
        //split hh:mm:ss into  hours minutes seconds
        val split = runLength.split(":").map { it.trim() }
        //add the milliseconds of the hours, minutes, and seconds up for the total time
        return (split[0].toLong() * (60 * 60 * 1000)) + (split[1].toLong() * (60 * 1000)) + (split[2].toLong() * (1000))
    }

    fun getTimeDiff(currentTime: Long, targetTime: Long): Long {
        return targetTime - currentTime
    }

    fun getDays(time: Long): Long {
        return time / (24L * 60L * 60L * 1000L)
    }

    fun getHours(time: Long): Long {
        return time / (60L * 60L * 1000L)
    }

    fun getMinutes(time: Long): Long {
        return time / (60L * 1000L)
    }

    fun getSeconds(time: Long): Long {
        return time / (1000L)
    }

    fun getRelativeTimeFromMilliseconds(time: Long): String {
        val targetDate = Date(time)
        val today = Date(System.currentTimeMillis())

        val targetCalender = Calendar.getInstance(TimeZone.getDefault())
        targetCalender.time = targetDate
        val todayCalender = Calendar.getInstance(TimeZone.getDefault())
        todayCalender.time = today

        val days = calendarDaysBetween(todayCalender, targetCalender)

        val s = "hh:mm a"
        val hourMinuteDateFormat = SimpleDateFormat(s, Locale.getDefault())
        return when {
            days <= 0 -> "at " + hourMinuteDateFormat.format(targetDate)
            days == 1L -> "Tomorrow, " + hourMinuteDateFormat.format(targetDate)
            days < 7 -> SimpleDateFormat("EEE, $s", Locale.getDefault()).format(targetDate)
            else -> SimpleDateFormat("MMM dd, $s", Locale.getDefault()).format(targetDate)
        }
    }

    fun getTimeFromMilliseconds(time: Long): String {
        val targetDate = Date(time)
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(targetDate)
    }

    //https://stackoverflow.com/questions/19462912/how-to-get-number-of-days-between-two-calendar-instance

    /**
     * Compute the number of calendar days between two Calendar objects.
     * The desired value is the number of days of the month between the
     * two Calendars, not the number of milliseconds' worth of days.
     * @param startCal The earlier calendar
     * @param endCal The later calendar
     * @return the number of calendar days of the month between startCal and endCal
     */
    fun calendarDaysBetween(startCal: Calendar, endCal: Calendar): Long {

        // Create copies so we don't update the original calendars.

        val start = Calendar.getInstance()
        start.timeZone = startCal.timeZone
        start.timeInMillis = startCal.timeInMillis

        val end = Calendar.getInstance()
        end.timeZone = endCal.timeZone
        end.timeInMillis = endCal.timeInMillis

        // Set the copies to be at midnight, but keep the day information.

        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)

        end.set(Calendar.HOUR_OF_DAY, 0)
        end.set(Calendar.MINUTE, 0)
        end.set(Calendar.SECOND, 0)
        end.set(Calendar.MILLISECOND, 0)

        // At this point, each calendar is set to midnight on
        // their respective days. Now use TimeUnit.MILLISECONDS to
        // compute the number of full days between the two of them.

        return TimeUnit.MILLISECONDS.toDays(
                Math.abs(end.timeInMillis - start.timeInMillis))
    }

    fun getFormattedTime(diff: Long, showSeconds: Boolean = false, showMinutes: Boolean = false, showHours: Boolean = false): String {
        val dif = Math.abs(diff)

        val days = getDays(dif)
        val hours = getHours((dif - (days * (24L * 60L * 60L * 1000L))))
        val minutes = getMinutes((dif - (days * (24L * 60L * 60L * 1000L)) - (hours * (60L * 60L * 1000L))))
        val seconds = getSeconds((dif - (days * (24L * 60L * 60L * 1000L)) - (hours * (60L * 60L * 1000L)) - (minutes * (60L * 1000L))))

        var string = ""
        if (days > 0) {
            string += days.toString() + " day"
            if (days > 1) {
                string += "s"
            }
            string += " "
        }
        if (showHours && hours > 0) {
            string += hours.toString() + " hour"
            if (hours > 1) {
                string += "s"
            }
            string += " "
        }
        if (showMinutes && minutes > 0) {
            string += minutes.toString() + " minute"
            if (minutes > 1) {
                string += "s"
            }
            string += " "
        }
        if (showSeconds && seconds > 0) {
            string += seconds.toString() + " second"
            if (seconds > 1) {
                string += "s"
            }
            string += " "
        }

        if (diff < 0) {
            string += "ago "
        }
        return string.trim()
    }
}