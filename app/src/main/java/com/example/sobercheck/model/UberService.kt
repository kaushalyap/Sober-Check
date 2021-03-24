package com.example.sobercheck.model

import com.uber.sdk.android.core.UberSdk
import com.uber.sdk.rides.client.SessionConfiguration
import com.uber.sdk.android.rides.RideParameters


class UberService() {

    init {
        val config: SessionConfiguration = SessionConfiguration.Builder() // mandatory
            .setClientId("<CLIENT_ID>") // required for enhanced button features
            .setServerToken("<TOKEN>") // required for implicit grant authentication
            .setRedirectUri("<REDIRECT_URI>") // optional: set sandbox as operating environment
            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
            .build()
        UberSdk.initialize(config)
    }

    fun setRideParams(): RideParameters? {
        return RideParameters.Builder() // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
            .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d") // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
            .setDropoffLocation(
                37.775304,
                -122.417522,
                "Uber HQ",
                "1455 Market Street, San Francisco"
            ) // Required for pickup estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of pickup location
            .setPickupLocation(
                37.775304,
                -122.417522,
                "Uber HQ",
                "1455 Market Street, San Francisco"
            )
            .build()
    }
}