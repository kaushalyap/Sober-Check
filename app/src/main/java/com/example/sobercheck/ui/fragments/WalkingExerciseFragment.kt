package com.example.sobercheck.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentWalkingExerciseBinding
import com.example.sobercheck.model.CountDown
import com.example.sobercheck.model.MachineLearning
import com.example.sobercheck.model.Sensor
import kotlinx.coroutines.launch

class WalkingExerciseFragment : Fragment() {

    private lateinit var sensor: Sensor
    private var _binding: FragmentWalkingExerciseBinding? = null
    internal val binding get() = _binding!!
    private val args: SelfieFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkingExerciseBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        setOnClickListener()
    }

    private fun setOnClickListener() {
        collectAccelerometerReadings()
        binding.btnDone.setOnClickListener {
            lifecycleScope.launch {
                val isDrunkFromSelfie = args.isDrunkFromSelfie
                val isDrunkFromAccelerometer =
                    MachineLearning().predictFromAccelerometer(sensor.movement)

                Log.d(
                    TAG,
                    "IsDrunkFromSelfie : $isDrunkFromSelfie, IsDrunkFromAccel : $isDrunkFromAccelerometer"
                )
                if (isDrunkFromAccelerometer || isDrunkFromSelfie) {
                    findNavController().navigate(R.id.action_walkingExercise_to_drunk)
                } else {
                    findNavController().navigate(R.id.action_walkingExercise_to_sober)
                }
            }
        }
    }

    private fun collectAccelerometerReadings() {
        sensor = Sensor()
        sensor.registerSensor(requireActivity())

        val countDownTimer =
            object : CountDownTimer(CountDown.TIME_10_SECONDS.time, CountDown.STEP.time) {

                override fun onTick(millisUntilFinished: Long) {
                    val inSeconds = millisUntilFinished / 1000
                    binding.txtCountDown.text = "$inSeconds seconds"
                }

                override fun onFinish() {
                    sensor.unRegisterSensor()
                    binding.btnDone.visibility = View.VISIBLE
                }
            }
        countDownTimer.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG: String = "WalkingExerciseFragment"
    }
}