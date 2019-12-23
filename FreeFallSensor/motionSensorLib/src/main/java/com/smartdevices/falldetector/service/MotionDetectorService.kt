package com.smartdevices.falldetector.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.smartdevices.falldetector.R
import com.smartdevices.falldetector.model.AccelerometerData
import com.smartdevices.falldetector.model.EventData
import org.greenrobot.eventbus.EventBus
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * MotionDetectorService
 *
 * Foreground service for monitor freefall motion of device.
 * will update the caller application using eventbus
 *
 */
class MotionDetectorService : Service(), SensorEventListener {

    private val lowThreshold = 1.5
    private val highThreshold = 14.0
    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private val sensorDataFilter = AccelerometerData();
    var eventData: EventData? = null
    var count = 0
    private var isFalling = false
    var max = false
    var curTime = 0L
    var timeDelta = 0L
    private val CHANNELID = "MotionDetectorServiceChannel"

    override fun onCreate() {

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // focus in accelerometer
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager!!.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra("inputExtra")
        createNotificationChannel()
        val notification: Notification = getNotification(input, getPendingIntent())
        startForeground(1, notification)
        // If we get killed, after returning from here, restart
        return START_STICKY
    }


    /**
     * getPendingIntent
     *  returns pending intent
     */
    private fun getPendingIntent(): PendingIntent? {
        val notificationIntent = Intent(this, applicationContext::class.java)
        return PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
    }

    /**
     * This is the method that can be called to get a Notification from input
     */
    private fun getNotification(input: String?, pendingIntent: PendingIntent?): Notification {
        return NotificationCompat.Builder(this, CHANNELID)
            .setContentTitle("Motion Tracking")
            .setContentText(input)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .build()
    }


    override fun onDestroy() {
        mSensorManager!!.unregisterListener(this)
    }

    /**
     * This is the method that can be called to create the Notification
     */
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNELID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    /**
     * This is the method that can be called to update the Notification
     */
    private fun updateNotification(notificationText: String) {
        val notification: Notification = getNotification(notificationText, getPendingIntent())
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(1, notification)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //ignored for the sample application
    }

    override fun onSensorChanged(event: SensorEvent) {

        var updatedTime: Long = 0

        //check whether data from ACCELEROMETER sensor
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            //extract values
            sensorDataFilter.x = event.values[0]
            sensorDataFilter.y = event.values[1]
            sensorDataFilter.z = event.values[2]

            //find the amplitude of motion event
            var amplitude: Float = sensorDataFilter.findAmplitude()
            Log.d("SensorData", "amp " + amplitude)

            //amplitude<lower threshold of normal activities
            if (amplitude < lowThreshold) {

                //check if already falling
                if (!isFalling) {
                    //freefall started, collect timestamp for calculate duration of fall
                    curTime = System.currentTimeMillis()
                    isFalling = true
                    Log.d("Free fall", "started fall")

                }
            }

            if (isFalling) {
                count++

                //amplitude>higher threshold of normal activities
                if (amplitude >= highThreshold) {

                    //probably freefall motion ended, collect timestamp, reset flags
                    max = true
                    isFalling = false
                    updatedTime = System.currentTimeMillis()
                    //calculate the timeDelta
                    timeDelta = updatedTime - curTime
                    eventData =
                        EventData(getTimeString(curTime), getTimeString(updatedTime), timeDelta)
                }
            }
        }

        //check if low threshold happen and after got high threshold
        if (isFalling && max) {

            //neglect motions less life
            if (timeDelta > 60) {

                //Freefall motion, update notification and repository
                Log.d("TimeDelta", "difference in time $timeDelta in milliseconds")
                updateNotification("$timeDelta in milliseconds")
                eventData!!.duration = timeDelta
                updateRepository(eventData!!)
            }

            //reset all flags
            curTime = 0
            count = 0
            isFalling = false
            max = false
        }

        //might be normal activity or sensor noise
        if (count > 4) {

            //reset all flags
            count = 0
            isFalling = false
            max = false
            curTime = 0
            timeDelta = 0
        }
    }

    /**
     * getTimeString
     *
     * returns a string representation of time from Milliseconds
     */
    private fun getTimeString(time: Long): String {
        val formatter: DateFormat = SimpleDateFormat("HH:mm:ss.SSSSS", Locale.US)
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"))
        return formatter.format(Date(time))
    }

    /**
     * updateRepository
     *
     * update the database about the observed event
     */
    private fun updateRepository(eventData: EventData) {
        EventBus.getDefault().post(eventData)
    }

}

