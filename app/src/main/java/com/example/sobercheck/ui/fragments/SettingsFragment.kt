package com.example.sobercheck.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.sobercheck.R
import com.example.sobercheck.ui.activities.MainActivity
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructLocationPermissionRequest
import permissions.dispatcher.ktx.constructPermissionsRequest


class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var mainActivity: MainActivity
    private lateinit var prefEmergencyContact: EditTextPreference
    private lateinit var callPermissionsRequester: PermissionsRequester
    private lateinit var smsPermissionsRequester: PermissionsRequester
    private lateinit var locationPermissionsRequester: PermissionsRequester
    private lateinit var internetPermissionsRequester: PermissionsRequester


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        init()
    }

    private fun init() {
        setupPreferenceListeners()
        mainActivity = activity as MainActivity
    }

    private fun setupPreferenceListeners() {

        setSharedPreferenceChangeListeners()

        prefEmergencyContact = (findPreference(getString(R.string.pref_emergency_contact))
            ?: return)
        prefEmergencyContact.setOnPreferenceClickListener {
            val contactPickerIntent = Intent(Intent.ACTION_PICK)
            contactPickerIntent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT)
            true
        }

        val prefDropOffLocation: EditTextPreference? =
            findPreference(getString(R.string.pref_drop_off_location))

        prefDropOffLocation?.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                preference.text
            }
    }

    private fun setSharedPreferenceChangeListeners() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val listener =
            OnSharedPreferenceChangeListener { prefs, key ->
                when {
                    key.equals(getString(R.string.pref_sms)) -> {
                        if (prefs.getBoolean(getString(R.string.pref_sms), false))
                            smsPermissionsRequester.launch()
                    }
                    key.equals(getString(R.string.pref_call)) -> {
                        if (prefs.getBoolean(getString(R.string.pref_call), false))
                            callPermissionsRequester.launch()
                    }
                    key.equals(getString(R.string.pref_uber)) -> {
                        if (prefs.getBoolean(getString(R.string.pref_uber), false)) {
                            internetPermissionsRequester.launch()
                            locationPermissionsRequester.launch()
                        }
                    }
                }
            }
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONTACT_PICKER_RESULT && resultCode == RESULT_OK) {

            val contactUri = data?.data
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val cursor = contactUri?.let {
                context?.contentResolver?.query(
                    it, projection,
                    null, null, null
                )
            }

            if (cursor != null && cursor.moveToFirst()) {
                val numberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val number = cursor.getString(numberIndex)

                val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
                with(sharedPref.edit()) {
                    putString(getString(R.string.pref_emergency_contact), number)
                    apply()
                }

                prefEmergencyContact =
                    (findPreference(getString(R.string.pref_emergency_contact)) ?: return)
                prefEmergencyContact.summaryProvider =
                    Preference.SummaryProvider<EditTextPreference> { preference ->
                        preference.text
                    }
            }
            cursor?.close()
        }
    }

    private fun sendSMS() {}
    private fun makeCall() {}
    private fun getCurrentLocation() {}
    private fun getAddress() {}

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
        mainActivity.showPermissionRationaleDialog(
            R.string.permission_internet_address_rationale,
            request
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

    companion object {
        const val TAG: String = "SettingsFragment"
        const val CONTACT_PICKER_RESULT: Int = 1
    }
}