package com.example.sobercheck.ui.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.sobercheck.R

class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}