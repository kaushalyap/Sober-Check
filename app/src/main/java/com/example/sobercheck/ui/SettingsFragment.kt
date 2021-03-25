package com.example.sobercheck.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.sobercheck.R

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var mainActivity: MainActivity

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}