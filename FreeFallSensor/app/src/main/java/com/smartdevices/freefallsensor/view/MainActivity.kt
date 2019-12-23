package com.smartdevices.freefallsensor.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.smartdevices.falldetector.service.MotionDetectorService
import com.smartdevices.freefallsensor.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        startService()
    }

    fun startService() {
        val serviceIntent = Intent(this, MotionDetectorService::class.java)
        serviceIntent.putExtra("inputExtra", "Waiting for a fall event....")
        ContextCompat.startForegroundService(this, serviceIntent)
    }
}
