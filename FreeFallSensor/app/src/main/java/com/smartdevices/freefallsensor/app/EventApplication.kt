package com.smartdevices.freefallsensor.app

import android.app.Application
import android.util.Log
import com.smartdevices.falldetector.model.EventData
import com.smartdevices.freefallsensor.database.EventRepository
import com.smartdevices.freefallsensor.models.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class EventApplication : Application() {
    private var registered = false
    private val TAG = "EventBroadcastReceiver"

    companion object {
        lateinit var repository: EventRepository
    }

    fun setRepository(repo: EventRepository) {
        repository = repo
    }

    fun getRepository(): EventRepository {
        return repository
    }

    override fun onTerminate() {
        super.onTerminate()

        EventBus.getDefault().unregister(this)
        registered = false

    }


    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        registered = true
    }

    @Subscribe(priority = 1)
    fun onMessage(event: EventData) = runBlocking(Dispatchers.Default) {
        Log.d(TAG, "" + event)
        var eventData = Event(0, event.startTime, event.endTime, event.duration.toString())
        repository.insert(eventData)

    }

}