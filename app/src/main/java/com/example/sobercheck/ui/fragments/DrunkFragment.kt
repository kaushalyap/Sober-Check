package com.example.sobercheck.ui.fragments

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentDrunkBinding
import com.example.sobercheck.model.AutomatedResponse
import com.example.sobercheck.model.Telephony
import com.example.sobercheck.model.UberService
import com.example.sobercheck.ui.activities.MainActivity
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructLocationPermissionRequest
import permissions.dispatcher.ktx.constructPermissionsRequest


class DrunkFragment : Fragment() {

    private lateinit var responded: BooleanArray
    private lateinit var mediaPlayer: MediaPlayer
    private var _binding: FragmentDrunkBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private lateinit var callPermissionsRequester: PermissionsRequester
    private lateinit var smsPermissionsRequester: PermissionsRequester
    private lateinit var locationPermissionsRequester: PermissionsRequester
    private lateinit var uberService: UberService
    private lateinit var telephony: Telephony

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrunkBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        mainActivity = activity as MainActivity
        mediaPlayer = MediaPlayer.create(context, R.raw.drunk_warning)
        setButtonClickListeners()
        uberService = UberService()
        telephony = Telephony()
        automatedRespond()
    }

    private fun automatedRespond() {
        val automatedResponse = AutomatedResponse()
        AutomatedResponse().respond(requireActivity(), requireContext(), mediaPlayer)
        responded = automatedResponse.responded

        @Suppress("CascadeIf")
        if (responded[1]) {
            binding.btnSms.visibility = View.GONE
            Toast.makeText(context, "Message Sent!", Toast.LENGTH_SHORT).show()
        } else if (responded[2]) {
            binding.btnCall.visibility = View.GONE
        } else if (responded[3]) {
            binding.btnRideRequest.visibility = View.GONE
        } else
            Log.d(TAG, "Alert ON")
    }


    private fun setButtonClickListeners() {
        binding.btnCall.setOnClickListener {
            callPermissionsRequester.launch()
        }
        binding.btnSms.setOnClickListener {
            smsPermissionsRequester.launch()
        }
        binding.btnRideRequest.setOnClickListener {
            locationPermissionsRequester.launch()
            orderAnUber()
        }
    }

    private fun getCurrentLocation() {
        uberService.getCurrentLocation(requireContext())
    }

    private fun orderAnUber() {
        binding.btnRideRequest.setRideParameters(uberService.getRideParameters(requireContext()))
        uberService.getUberDeepLink(requireContext()).execute()
    }

    private fun sendSMS() {
        telephony.sendSMS(requireContext())
    }

    private fun makeCall() {
        telephony.makeCall(requireActivity())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callPermissionsRequester = constructPermissionsRequest(
            Manifest.permission.CALL_PHONE,
            onShowRationale = ::onCallShowRationale,
            onPermissionDenied = ::onCallDenied,
            onNeverAskAgain = ::onCallNeverAskAgain,
            requiresPermission = ::makeCall
        )
        smsPermissionsRequester = constructPermissionsRequest(
            Manifest.permission.SEND_SMS,
            onShowRationale = ::onSmsShowRationale,
            onPermissionDenied = ::onSmsDenied,
            onNeverAskAgain = ::onSmsNeverAskAgain,
            requiresPermission = ::sendSMS
        )

        locationPermissionsRequester = constructLocationPermissionRequest(
            LocationPermission.FINE,
            onShowRationale = ::onLocationShowRationale,
            onPermissionDenied = ::onLocationDenied,
            onNeverAskAgain = ::onLocationNeverAskAgain,
            requiresPermission = ::getCurrentLocation
        )
    }


    private fun onLocationNeverAskAgain() {
        Toast.makeText(
            context,
            R.string.permission_location_never_ask_again,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onLocationDenied() {
        Toast.makeText(context, R.string.permission_location_denied, Toast.LENGTH_SHORT).show()
    }

    private fun onLocationShowRationale(request: PermissionRequest) {
        mainActivity.showPermissionRationaleDialog(R.string.permission_location_rationale, request)
    }

    private fun onSmsNeverAskAgain() {
        Toast.makeText(
            context,
            R.string.permission_sms_never_ask_again,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onSmsDenied() {
        Toast.makeText(context, R.string.permission_sms_denied, Toast.LENGTH_SHORT).show()
    }

    private fun onSmsShowRationale(request: PermissionRequest) {
        mainActivity.showPermissionRationaleDialog(R.string.permission_call_rationale, request)
    }

    private fun onCallNeverAskAgain() {
        Toast.makeText(
            context,
            R.string.permission_call_never_ask_again,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onCallDenied() {
        Toast.makeText(context, R.string.permission_call_denied, Toast.LENGTH_SHORT).show()
    }

    private fun onCallShowRationale(request: PermissionRequest) {
        mainActivity.showPermissionRationaleDialog(R.string.permission_call_rationale, request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mediaPlayer.stop()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }

    companion object {
        const val TAG: String = "DrunkFragment"
    }
}