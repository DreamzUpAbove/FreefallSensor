package com.smartdevices.freefallsensor.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_table")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val eventId: Int = 0,
    val startTime: String = "",
    val endTime: String = "",
    val duration: String = ""
)