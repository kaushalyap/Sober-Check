package com.example.sobercheck.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.sobercheck.R

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var mainActivity: MainActivity

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        hideFabBottomAppBar()
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    private fun hideFabBottomAppBar() {
        mainActivity = activity as MainActivity
        mainActivity.hideFabBottomAppBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.showFabBottomAppBar()
    }
}