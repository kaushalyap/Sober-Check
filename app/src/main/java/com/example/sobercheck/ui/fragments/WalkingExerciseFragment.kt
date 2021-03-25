package com.example.sobercheck.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentWalkingExerciseBinding
import com.example.sobercheck.ui.activities.MainActivity

class WalkingExerciseFragment : Fragment() {

    private var _binding: FragmentWalkingExerciseBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalkingExerciseBinding.inflate(inflater, container, false)

        binding.btnDone.setOnClickListener {
            findNavController().navigate(R.id.action_walkingExercise_to_drunk)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}