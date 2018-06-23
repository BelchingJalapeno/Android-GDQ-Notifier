package com.belchingjalapeno.agdqschedulenotifier

/**
 * Time related calculations and formatting
 */
class TimeCalculator {

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