package com.example.sobercheck.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.sobercheck.R



class SettingsFragment : PreferenceFragmentCompat() {

    private var prefEmergencyContact: Preference? = null
    private var prefDropOffLocation: EditTextPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setupPreferenceListeners()
    }

    private fun setupPreferenceListeners() {
        prefEmergencyContact = findPreference(getString(R.string.pref_emergency_contact))

        prefEmergencyContact?.setOnPreferenceClickListener {
            val contactPickerIntent = Intent(Intent.ACTION_PICK)
            contactPickerIntent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT)
            true
        }

        prefDropOffLocation = findPreference(getString(R.string.pref_drop_off_location))

        prefDropOffLocation?.summaryProvider =
            Preference.SummaryProvider<EditTextPreference> { preference ->
                preference.text
            }
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

                prefEmergencyContact = findPreference(getString(R.string.pref_emergency_contact))
                prefEmergencyContact?.summaryProvider =
                    Preference.SummaryProvider<EditTextPreference> { preference ->
                        preference.text
                    }
            }
            cursor?.close()
        }
    }

    companion object {
        const val TAG: String = "SettingsFragment"
        const val CONTACT_PICKER_RESULT: Int = 1
    }
}