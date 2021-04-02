package com.example.sobercheck.model

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.preference.PreferenceManager
import com.example.sobercheck.R
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.uber.sdk.android.core.UberSdk
import com.uber.sdk.android.rides.RideParameters
import com.uber.sdk.android.rides.RideRequestDeeplink
import com.uber.sdk.core.auth.Scope
import com.uber.sdk.core.client.SessionConfiguration
import java.util.*

class UberService {

    private lateinit var knowName: String
    private lateinit var addressLine: String
    private var config: SessionConfiguration = SessionConfiguration.Builder().setClientId(CLIENT_ID)
        .setScopes(listOf(Scope.PROFILE, Scope.RIDE_WIDGETS)).build()
    private var uberPickupLocation: UberLocation
    private lateinit var uberDropOffLocation: UberLocation
    private var longitude: Double = 0.0000
    private var latitude: Double = 0.0000

    init {
        UberSdk.initialize(config)
        uberPickupLocation = UberLocation(null, null, null, null)
    }

    private fun setDropOffLocation(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val dropOffLocation =
            sharedPreferences.getBoolean(
                context.resources.getString(R.string.pref_drop_off_location),
                false
            )
                .toString()
        uberDropOffLocation = UberLocation(null, null, null, dropOffLocation)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context) {
        val cancellationTokenSource = CancellationTokenSource()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
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

    fun setAddress(context: Context) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(latitude, longitude, 1)
        addressLine = address[0].getAddressLine(0)
        knowName = address[0].featureName.toString()
    }

    private fun setPickupLocation(result: Array<Double>): UberLocation {
        latitude = result[0]
        longitude = result[1]
        uberPickupLocation = UberLocation(longitude, latitude, knowName, addressLine)
        return uberPickupLocation
    }


    fun getUberDeepLink(context: Context): RideRequestDeeplink {
        return RideRequestDeeplink.Builder(context).setSessionConfiguration(config)
            .setRideParameters(getRideParameters(context)).build()
    }

    fun getRideParameters(context: Context): RideParameters {
        setDropOffLocation(context)
        return RideParameters.Builder()
            .setPickupLocation(
                uberPickupLocation.latitude,
                uberPickupLocation.longitude,
                uberPickupLocation.nickName,
                uberPickupLocation.address
            )
            .setDropoffLocation(
                uberDropOffLocation.latitude,
                uberDropOffLocation.longitude,
                uberDropOffLocation.nickName,
                uberDropOffLocation.address
            )
            .build()
    }

    companion object {
        // Take Client ID from https://developer.uber.com/dashboard/
        const val CLIENT_ID: String = "Xr5TFJyIAFj074yHrYm2txrXxCDdWA8m"
    }
}