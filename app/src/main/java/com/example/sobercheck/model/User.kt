package com.example.sobercheck.model

class User {
    private var isDrunk: Boolean = false
    private lateinit var address: String
    private lateinit var emergencyContact: String
    private var userSettings: UserSettings? = null
    private lateinit var machineLearning: MachineLearning

    fun isDrunk(): Boolean {
        machineLearning = MachineLearning()
        val isDrunkFromSelfie = machineLearning.predictFromSelfie()
        val isDrunkFromAccelerometer = machineLearning.predictFromAccelerometer()

        if (isDrunkFromSelfie && isDrunkFromAccelerometer)
            isDrunk = true

        return isDrunk
    }
}