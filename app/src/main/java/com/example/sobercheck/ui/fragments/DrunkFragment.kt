package com.example.sobercheck.ui.fragments

import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sobercheck.databinding.FragmentDrunkBinding
import com.example.sobercheck.model.UberService
import com.example.sobercheck.ui.activities.MainActivity


class DrunkFragment : Fragment() {

    private var _binding: FragmentDrunkBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrunkBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        setButtonClickListeners()
        binding.btnRideRequest.setRideParameters(UberService().getRideRequest())
        return binding.root
    }


    private fun setButtonClickListeners() {

        binding.btnCall.setOnClickListener {
        }

        binding.btnSms.setOnClickListener {
        }
    }

    private fun getLocation() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}