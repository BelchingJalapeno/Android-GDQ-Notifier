package com.belchingjalapeno.agdqschedulenotifier

class EventFilter {
    var notificationOnly = false
        private set
    var query: String = " "
        private set
    private val changeListeners: MutableList<FilterChangedListener> = mutableListOf()

    fun changeFilter(notificationOnly: Boolean, query: String) {
        this.notificationOnly = notificationOnly
        this.query = query
        changeListeners.forEach { it.changed(notificationOnly, query) }
    }

    fun changeFilter(query: String) {
        this.query = query
        changeListeners.forEach { it.changed(notificationOnly, query) }
    }

    fun changeFilter(notificationOnly: Boolean) {
        this.notificationOnly = notificationOnly
        changeListeners.forEach { it.changed(notificationOnly, query) }
    }

    /**
     * this method calls the listener immediately after registering
     */
    fun addListener(listener: FilterChangedListener) {
        if (!this.changeListeners.contains(listener)) {
            this.changeListeners.add(listener)
        }
        listener.changed(notificationOnly, query)
    }

    fun removeListener(listener: FilterChangedListener) {
        this.changeListeners.remove(listener)
    }
}

interface FilterChangedListener {
    /**
     * gets called immediately after registering
     */
    fun changed(notificationOnly: Boolean, query: String)
}
