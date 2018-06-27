package com.belchingjalapeno.agdqschedulenotifier


import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class TimeBetweenTest {
    @Test
    fun addition_isCorrect() {
        val timeCalc = TimeFormatter()
        val targetTime = 1000L
        val currentTime = 300L

        val expectedTime = 700L

        val calculatedTime = timeCalc.getTimeDiff(currentTime, targetTime)

        assertThat(calculatedTime, equalTo(expectedTime))
    }

    @Test
    fun days() {
        val targetTime = 86400000L
        val expected = 1L

        assertThat(TimeFormatter().getDays(targetTime), equalTo(expected))
    }

    @Test
    fun hours() {
        val targetTime = 86400000L
        val expected = 24L

        assertThat(TimeFormatter().getHours(targetTime), equalTo(expected))
    }

    @Test
    fun minutes() {
        val targetTime = 86400000L
        val expected = 1440L

        assertThat(TimeFormatter().getMinutes(targetTime), equalTo(expected))
    }

    @Test
    fun seconds() {
        val targetTime = 86400000L
        val expected = 86400L

        assertThat(TimeFormatter().getSeconds(targetTime), equalTo(expected))
    }

    @Test
    fun formatTime() {
        val targetTime = 86400000L + (60L * 60L * 1000L) + (60L * 1000L) + 1000L
        val expected = "1 day 1 hour 1 minute 1 second"

        assertThat(TimeFormatter().getFormattedTime(targetTime, true, true, true), equalTo(expected))
    }

    @Test
    fun formatTimeNegative() {
        val targetTime = -86400000L - (60L * 60L * 1000L) - (60L * 1000L) - 1000L
        val expected = "1 day 1 hour 1 minute 1 second ago"

        assertThat(TimeFormatter().getFormattedTime(targetTime, true, true, true), equalTo(expected))
    }

    @Test
    fun formatTime2() {
        val targetTime = 2 * 86400000L + 2 * (60L * 60L * 1000L) + 2 * (60L * 1000L) + 62 * 1000L
        val expected = "2 days 2 hours 3 minutes 2 seconds"

        assertThat(TimeFormatter().getFormattedTime(targetTime, true, true, true), equalTo(expected))
    }

    @Test
    fun formatTime2Negative() {
        val targetTime = -2 * 86400000L - 2 * (60L * 60L * 1000L) - 2 * (60L * 1000L) - 62 * 1000L
        val expected = "2 days 2 hours 3 minutes 2 seconds ago"

        assertThat(TimeFormatter().getFormattedTime(targetTime, true, true, true), equalTo(expected))
    }
}