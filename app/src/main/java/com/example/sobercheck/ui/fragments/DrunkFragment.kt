package com.example.sobercheck.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sobercheck.databinding.FragmentDrunkBinding
import com.example.sobercheck.ui.activities.MainActivity
import com.example.sobercheck.ui.utils.PermissionCode


class DrunkFragment : Fragment() {

    private var _binding: FragmentDrunkBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrunkBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        setButtonClickListeners()
        return binding.root
    }


    private fun setButtonClickListeners() {
        binding.rideRequestButton.setOnClickListener {

//            val uber = UberService()
//            uber.setRideParams()
        }

        binding.btnCall.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), "Permission already granted!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                mainActivity.makeRequest(
                    Manifest.permission.CALL_PHONE,
                    PermissionCode.MAKE_CALL.requestCode
                )
            }
            mainActivity.makeCall()
        }

        binding.btnSms.setOnClickListener {
            mainActivity.makeRequest(
                Manifest.permission.SEND_SMS,
                PermissionCode.SEND_SMS.requestCode
            )
            mainActivity.sendSMS()
        }
    }

//    private fun getCurrentLocation(): UberLocation{
//        return UberLocation
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}