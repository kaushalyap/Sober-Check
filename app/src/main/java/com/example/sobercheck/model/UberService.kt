package com.example.sobercheck.model

import com.uber.sdk.android.core.UberSdk
import com.uber.sdk.android.rides.RideParameters
import com.uber.sdk.rides.client.SessionConfiguration


const val CLIENT_ID = "Xr5TFJyIAFj074yHrYm2txrXxCDdWA8m"

class UberService {

    init {
        val config: SessionConfiguration = SessionConfiguration.Builder()
            .setClientId(CLIENT_ID).build()
        UberSdk.initialize(config)
    }

    fun getRideRequest(): RideParameters {

        return RideParameters.Builder()
            .setDropoffLocation(
                37.775304,
                -122.417522,
                "Uber HQ",
                "1455 Market Street, San Francisco"
            )
            .setPickupLocation(
                37.775304,
                -122.417522,
                "Uber HQ",
                "1455 Market Street, San Francisco"
            )
            .build()
    }

}