package com.belchingjalapeno.agdqschedulenotifier

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Test

class EventFilterTest {

    private val filter = EventFilter()

    @After
    fun tearDown() {
        filter.clearFilters()
        filter.reset()
    }

    @Test
    fun changeFilterBoth() {
        filter.addListener(object : SkipFirstFilter() {
            override fun change(notificationOnly: Boolean, query: String) {
                assertThat(notificationOnly, equalTo(true))
                assertThat(query, equalTo("query"))
            }
        })
        filter.changeFilter(true, "query")
    }

    @Test
    fun changeFilterNotificationOnly() {
        filter.addListener(object : SkipFirstFilter() {
            override fun change(notificationOnly: Boolean, query: String) {
                assertThat(notificationOnly, `is`(true))
                assertThat(query, equalTo(""))
            }
        })
        filter.changeFilter(true)
    }

    @Test
    fun changeFilterQuery() {
        filter.addListener(object : SkipFirstFilter() {
            override fun change(notificationOnly: Boolean, query: String) {
                assertThat(notificationOnly, equalTo(false))
                assertThat(query, equalTo("query"))
            }
        })
        filter.changeFilter("query")
    }

    @Test
    fun reset() {
        filter.changeFilter(true, "query")

        assertThat(filter.notificationOnly, equalTo(true))
        assertThat(filter.query, equalTo("query"))
    }
}

//needed because adding listeners calls the changed function right away
private abstract class SkipFirstFilter : FilterChangedListener {
    private var first = true

    override fun changed(notificationOnly: Boolean, query: String) {
        if (first) {
            first = false
            return
        } else {
            change(notificationOnly, query)
        }
    }

    abstract fun change(notificationOnly: Boolean, query: String)

}