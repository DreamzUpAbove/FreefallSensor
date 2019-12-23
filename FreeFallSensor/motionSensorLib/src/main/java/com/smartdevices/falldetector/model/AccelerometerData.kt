package com.smartdevices.falldetector.model

import kotlin.math.sqrt

class AccelerometerData {
    var x = 0f
    var y = 0f
    var z = 0f

    override fun toString(): String {
        return "AccelerometerData{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}'
    }

    /**
     * findAmplitude
     * returns the amplitude from x,y,z values
     */
    fun findAmplitude(): Float {
        return (sqrt((x * x) + (y * y) + (z * z)))
    }
}