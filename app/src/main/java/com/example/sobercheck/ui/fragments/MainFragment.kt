package com.example.sobercheck.ui.fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentMainBinding
import com.example.sobercheck.ui.activities.MainActivity
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest


class MainFragment : Fragment() {

    private lateinit var internetPermissionsRequester: PermissionsRequester
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        showFabBottomAppBar()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val address = sharedPreferences.getString(getString(R.string.pref_drop_off_location), "")
        binding.textView.text = address
        return binding.root
    }

    private fun showFabBottomAppBar() {
        mainActivity.showFabBottomAppBar()
    }

    private fun hideFabBottomAppBar() {
        mainActivity.hideFabBottomAppBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        hideFabBottomAppBar()
    }

    private fun downloadModel() {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi().build()
        FirebaseModelDownloader.getInstance()
            .getModel("", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
            .addOnSuccessListener { customModel ->

                // Download complete. Depending on your app, you could enable the ML
                // feature, or switch from the local model to the remote model, etc.

                // The CustomModel object contains the local path of the model file,
                // which you can use to instantiate a TensorFlow Lite interpreter.
                Toast.makeText(context, "Model  download complete!", Toast.LENGTH_SHORT).show()

            }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        internetPermissionsRequester = constructPermissionsRequest(
            Manifest.permission.INTERNET,
            onShowRationale = ::onInternetShowRationale,
            onPermissionDenied = ::onInternetDenied,
            onNeverAskAgain = ::onInternetNeverAskAgain,
            requiresPermission = ::downloadModel
        )

    }

    private fun onInternetDenied() {
        Toast.makeText(context, R.string.permission_internet_denied, Toast.LENGTH_SHORT).show()
    }

    private fun onInternetNeverAskAgain() {
        Toast.makeText(
            context,
            R.string.permission_internet_never_ask_again,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onInternetShowRationale(request: PermissionRequest) {
        mainActivity.showRationaleDialog(R.string.permission_internet_rationale, request)

    }
}