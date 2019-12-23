package com.smartdevices.freefallsensor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.smartdevices.freefallsensor.app.EventApplication
import com.smartdevices.freefallsensor.database.EventRepository
import com.smartdevices.freefallsensor.database.EventRoomDatabase
import com.smartdevices.freefallsensor.models.Event
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val repository: EventRepository
    // LiveData gives us updated events when they change.
    val allEvents: LiveData<List<Event>>

    init {
        // Gets reference to EventDao from EventRoomDatabase to construct
        // the correct WordRepository.
        val eventDao = EventRoomDatabase.getDatabase(application, viewModelScope).eventDao()
        repository = EventRepository(eventDao)
        getApplication<EventApplication>().setRepository(repository)
        allEvents = repository.allEvents
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on the mainthread, blocking
     * the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called viewModelScope which we
     * can use here.
     */
    fun insert(event: Event) = viewModelScope.launch {
        repository.insert(event)
    }
}
