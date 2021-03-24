package com.example.sobercheck.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentDrunkBinding
import com.example.sobercheck.databinding.FragmentSoberBinding
import com.example.sobercheck.databinding.FragmentWalkingExerciseBinding

class WalkingExerciseFragment : Fragment() {

    private var _binding: FragmentWalkingExerciseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalkingExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}