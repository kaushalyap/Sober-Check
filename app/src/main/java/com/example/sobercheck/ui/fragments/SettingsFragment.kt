package com.example.sobercheck.ui.fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceFragmentCompat
import com.example.sobercheck.R
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var contactReadPermissionsRequester: PermissionsRequester


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contactReadPermissionsRequester = constructPermissionsRequest(
            Manifest.permission.CAMERA,
            onShowRationale = ::onContactReadShowRationale,
            onPermissionDenied = ::onContactReadDenied,
            onNeverAskAgain = ::onContactReadNeverAskAgain,
            requiresPermission = ::readContacts
        )
    }

    private fun readContacts() {
        TODO("Not yet implemented")
    }

    private fun onContactReadNeverAskAgain() {
        Toast.makeText(
            requireContext(),
            R.string.permission_contacts_never_ask_again,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onContactReadDenied() {
        Toast.makeText(requireContext(), R.string.permission_contacts_denied, Toast.LENGTH_SHORT)
            .show()
    }

    private fun onContactReadShowRationale(request: PermissionRequest) {
        showRationaleDialog(R.string.permission_contacts_rationale, request)
    }

    private fun showRationaleDialog(@StringRes messageResId: Int, request: PermissionRequest) {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.allow) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.deny) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(messageResId)
            .show()
    }
}