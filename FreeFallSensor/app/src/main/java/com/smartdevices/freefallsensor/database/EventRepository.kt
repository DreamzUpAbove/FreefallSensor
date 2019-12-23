package com.smartdevices.freefallsensor.database

import androidx.lifecycle.LiveData
import com.smartdevices.freefallsensor.models.Event

/**
 *  ***EventRepository
 *  Single Source of truth for entire application as repository
 *  all events are recorded and retrieved from this source
 */
class EventRepository(private val eventDao: EventDao) {

    /**
     * getAllEvents
     *
     *  retrieve all the recorded events from repository as List
     */
    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()


    /**
     * insert Event
     *
     *  insert a recorded events from repository
     */
    suspend fun insert(word: Event) {
        eventDao.insert(word)
    }
}
