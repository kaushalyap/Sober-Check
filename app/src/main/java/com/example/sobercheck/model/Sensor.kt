package com.example.sobercheck.model

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log


class Sensor : SensorEventListener {
    private val movement: ArrayList<AccelerationPoint> = ArrayList()

    fun initSensorManager(activity: Activity) {
        val sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private fun isUserStill() {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val point = AccelerationPoint(x, y, z)
            Log.d(TAG, "X = $x, Y = $y + Z = $z")
            movement.add(point)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    companion object {
        private const val TAG = "Accelerometer"
    }
}