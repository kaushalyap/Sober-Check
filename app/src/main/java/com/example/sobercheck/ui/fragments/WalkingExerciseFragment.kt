package com.example.sobercheck.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentWalkingExerciseBinding
import com.example.sobercheck.model.MachineLearning
import com.example.sobercheck.model.Sensor

class WalkingExerciseFragment : Fragment() {

    private var _binding: FragmentWalkingExerciseBinding? = null
    private val binding get() = _binding!!
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
        Sensor().initSensorManager(requireActivity())
        binding.btnDone.setOnClickListener {
            val isDrunkFromSelfie = args.isDrunkFromSelfie
            if (MachineLearning().predictFromAccelerometer() && isDrunkFromSelfie) {
                findNavController().navigate(R.id.action_walkingExercise_to_drunk)
            } else {
                findNavController().navigate(R.id.action_walkingExercise_to_sober)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG: String = "WalkingExerciseFragment"
    }
}