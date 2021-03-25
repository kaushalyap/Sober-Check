package com.example.sobercheck.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentSelfieBinding

class SelfieFragment : Fragment() {

    private var _binding: FragmentSelfieBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelfieBinding.inflate(inflater, container, false)

        hideFabBottomAppBar()

        binding.btnCamera.setOnClickListener {
            findNavController().navigate(R.id.action_selfie_to_walkingExercise)
            hideFabBottomAppBar()
        }
        return binding.root
    }

    private fun hideFabBottomAppBar() {
        mainActivity = activity as MainActivity
        mainActivity.hideFabBottomAppBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainActivity.showFabBottomAppBar()
    }


}