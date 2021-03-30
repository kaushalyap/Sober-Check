package com.example.sobercheck.ui.fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentMainBinding
import com.example.sobercheck.model.MachineLearning
import com.example.sobercheck.ui.activities.MainActivity
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
        internetPermissionsRequester.launch()
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
        MachineLearning().downloadModels()
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
        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.allow) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.deny) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage("messageResId")
            .show()
    }
}