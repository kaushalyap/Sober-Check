package com.example.sobercheck.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentDrunkBinding
import com.example.sobercheck.model.UberLocation
import com.example.sobercheck.ui.activities.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.uber.sdk.android.core.UberSdk
import com.uber.sdk.android.rides.RideParameters
import com.uber.sdk.android.rides.RideRequestDeeplink
import com.uber.sdk.core.auth.Scope
import com.uber.sdk.core.client.SessionConfiguration
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructLocationPermissionRequest
import permissions.dispatcher.ktx.constructPermissionsRequest
import java.util.*


const val MESSAGE_BODY: String =
    "Drunk detected!, Please look after the sender to avoid accidents. Sent by Sober Check app"
const val CLIENT_ID: String = "Xr5TFJyIAFj074yHrYm2txrXxCDdWA8m"

class DrunkFragment : Fragment() {


    private lateinit var config: SessionConfiguration
    private lateinit var deeplink: RideRequestDeeplink
    private lateinit var mediaPlayer: MediaPlayer
    private var _binding: FragmentDrunkBinding? = null
    internal val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity
    private lateinit var callPermissionsRequester: PermissionsRequester
    private lateinit var smsPermissionsRequester: PermissionsRequester
    private lateinit var locationPermissionsRequester: PermissionsRequester
    private lateinit var internetPermissionsRequester: PermissionsRequester

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var uberPickupLocation: UberLocation
    private lateinit var geocoder: Geocoder
    private lateinit var address: List<Address>
    private lateinit var addressLine: String
    private lateinit var knowName: String
    private var longitude: Double = 0.0000
    private var latitude: Double = 0.0000


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrunkBinding.inflate(inflater, container, false)
        initialSetup()
        return binding.root
    }

    private fun initialSetup() {
        mainActivity = activity as MainActivity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        mediaPlayer = MediaPlayer.create(context, R.raw.drunk_warning)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geocoder = Geocoder(context, Locale.getDefault())
        setButtonClickListeners()
        initUberSDK()

//        automateResponse()
        uberPickupLocation = UberLocation(null, null, null, null)
    }

    private fun initUberSDK() {
        config = SessionConfiguration.Builder().setClientId(CLIENT_ID)
            .setScopes(listOf(Scope.PROFILE, Scope.RIDE_WIDGETS)).build()
        UberSdk.initialize(config)
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

        internetPermissionsRequester = constructPermissionsRequest(
            Manifest.permission.INTERNET,
            onShowRationale = ::onInternetShowRationale,
            onPermissionDenied = ::onInternetDenied,
            onNeverAskAgain = ::onInternetNeverAskAgain,
            requiresPermission = ::getAddress
        )

        locationPermissionsRequester = constructLocationPermissionRequest(
            LocationPermission.FINE,
            onShowRationale = ::onLocationShowRationale,
            onPermissionDenied = ::onLocationDenied,
            onNeverAskAgain = ::onLocationNeverAskAgain,
            requiresPermission = ::getCurrentLocation
        )
    }

    private fun getAddress() {
        address = geocoder.getFromLocation(latitude, longitude, 1)
        addressLine = address[0].getAddressLine(0)
        knowName = address[0].featureName.toString()
    }

    private fun onInternetNeverAskAgain() {
        Toast.makeText(
            context,
            R.string.permission_internet_never_ask_again,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onInternetDenied() {
        Toast.makeText(context, R.string.permission_internet_denied, Toast.LENGTH_SHORT).show()
    }

    private fun onInternetShowRationale(request: PermissionRequest) {
        showRationaleDialog(R.string.permission_internet_rationale, request)
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
        showRationaleDialog(R.string.permission_location_rationale, request)
    }

    internal fun sendSMS() {
        val emergencyContactNo = getEmergencyContact()
        val smsManager: SmsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(emergencyContactNo, null, MESSAGE_BODY, null, null)
        Toast.makeText(context, getString(R.string.message_sent), Toast.LENGTH_LONG).show()
    }

    internal fun makeCall() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${getEmergencyContact()}"))
        activity?.startActivity(intent)
    }

    private fun getEmergencyContact(): String {
        return sharedPreferences.getString(getString(R.string.pref_emergency_contact), "")
            .toString()
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
        showRationaleDialog(R.string.permission_call_rationale, request)
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
        showRationaleDialog(R.string.permission_call_rationale, request)
    }

    private fun showRationaleDialog(@StringRes messageResId: Int, request: PermissionRequest) {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.allow) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.deny) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(messageResId)
            .show()
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

    private fun automateResponse() {

        val countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                val alertOn = sharedPreferences.getBoolean(getString(R.string.pref_alert), false)
                val callOn = sharedPreferences.getBoolean(getString(R.string.pref_call), false)
                val smsOn = sharedPreferences.getBoolean(getString(R.string.pref_sms), false)
                val orderAnUberOn =
                    sharedPreferences.getBoolean(getString(R.string.pref_uber), false)

                if (alertOn)
                    makeAlert()
                if (smsOn) {
                    sendSMS()
                    binding.btnSms.visibility = View.GONE
                    showDialog(getString(R.string.sms_dialog_body))
                }
                if (orderAnUberOn) {
                    orderAnUber()
                    binding.btnRideRequest.visibility = View.GONE
                    showDialog(getString(R.string.uber_dialog_body))
                }
                if (callOn)
                    makeCall()
            }
        }
        countDownTimer.start()
    }

    internal fun showDialog(messageBody: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.message_sent))
            .setMessage(messageBody)
            .setPositiveButton(
                getString(R.string.okay)
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

    internal fun makeAlert() {
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    private var cancellationTokenSource = CancellationTokenSource()

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )

        currentLocationTask.addOnCompleteListener { task: Task<Location> ->
            val result: Array<Double> = if (task.isSuccessful && task.result != null) {
                val result: Location = task.result
                arrayOf(result.latitude, result.longitude)
            } else {
                arrayOf(0.000, 0.000)
            }
            setPickupLocation(result)
        }
    }

    private fun setPickupLocation(result: Array<Double>): UberLocation {
        latitude = result[0]
        longitude = result[1]
        uberPickupLocation = UberLocation(result[1], result[0], knowName, addressLine)
        return uberPickupLocation
    }

    internal fun orderAnUber() {
        val savedDropOffLocation =
            sharedPreferences.getBoolean(getString(R.string.pref_drop_off_location), false)
                .toString()
        val dropOffLocation = UberLocation(null, null, null, savedDropOffLocation)
        val rideParams = RideParameters.Builder()
            .setPickupLocation(
                uberPickupLocation.latitude,
                uberPickupLocation.longitude,
                uberPickupLocation.nickName,
                uberPickupLocation.address
            )
            .setDropoffLocation(
                dropOffLocation.latitude,
                dropOffLocation.longitude,
                dropOffLocation.nickName,
                dropOffLocation.address
            )
            .build()
        binding.btnRideRequest.setRideParameters(rideParams)
        deeplink = RideRequestDeeplink.Builder(context).setSessionConfiguration(config)
            .setRideParameters(rideParams).build()
        deeplink.execute()


    }
}