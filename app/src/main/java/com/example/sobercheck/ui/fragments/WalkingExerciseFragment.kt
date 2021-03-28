package com.example.sobercheck.ui.fragments

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentWalkingExerciseBinding

class WalkingExerciseFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentWalkingExerciseBinding? = null
    private val binding get() = _binding!!
    private lateinit var sensorManager: SensorManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkingExerciseBinding.inflate(inflater, container, false)

        readAccelerometerData()
        binding.btnDone.setOnClickListener {
            findNavController().navigate(R.id.action_walkingExercise_to_drunk)
        }
        return binding.root
    }

    private fun readAccelerometerData() {
        sensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val xAcceleration = event.values[0]
            val yAcceleration = event.values[1]
            val zAcceleration = event.values[2]
            Log.d("Accelerometer", "x: $xAcceleration, y: $yAcceleration, z: $zAcceleration")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}