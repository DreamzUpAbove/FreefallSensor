package com.smartdevices.falldetector.model

class EventData(
    startTime: String,
    endTime: String,
    timeDelta: Long
) {
    var startTime = startTime
    var endTime = endTime
    var duration = timeDelta

    override fun toString(): String {
        return "AccelerometerData{" +
                ", startTime = " + startTime +
                ", endTime = " + endTime +
                ", duration = " + duration +
                '}'
    }


}