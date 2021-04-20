package com.example.sobercheck.model

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log


class Sensor : SensorEventListener {
    internal val movement: ArrayList<AccelerationPoint> = ArrayList()
    private lateinit var sensorManager: SensorManager

    fun registerSensor(activity: Activity) {
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun unRegisterSensor() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            Log.d(TAG, "timestamp = ${event.timestamp}, X = $x, Y = $y + Z = $z")

            val point = AccelerationPoint(x, y, z)
            if (movement.size != 50)
                movement.add(point)

            Log.d(TAG, movement.size.toString())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    companion object {
        private const val TAG = "Sensor"
    }
}