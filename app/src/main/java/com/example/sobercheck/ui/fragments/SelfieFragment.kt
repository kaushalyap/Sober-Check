package com.example.sobercheck.ui.fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentSelfieBinding
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

class SelfieFragment : Fragment() {

    private var _binding: FragmentSelfieBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraPermissionsRequester: PermissionsRequester


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelfieBinding.inflate(inflater, container, false)

        binding.btnCamera.setOnClickListener {
            cameraPermissionsRequester.launch()
            findNavController().navigate(R.id.action_selfie_to_walkingExercise)
        }
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        cameraPermissionsRequester = constructPermissionsRequest(
            Manifest.permission.CAMERA,
            onShowRationale = ::onCameraShowRationale,
            onPermissionDenied = ::onCameraDenied,
            onNeverAskAgain = ::onCameraNeverAskAgain,
            requiresPermission = ::openCamera
        )
    }

    private fun openCamera() {
        //  val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Environment.DIRECTORY_DOWNLOADS + "/temp.png")
        //  startActivityForResult(intent, 1)
    }

    private fun onCameraNeverAskAgain() {
        Toast.makeText(
            requireContext(),
            R.string.permission_camera_never_ask_again,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onCameraDenied() {
        Toast.makeText(requireContext(), R.string.permission_camera_denied, Toast.LENGTH_SHORT)
            .show()
    }

    private fun onCameraShowRationale(request: PermissionRequest) {
        showRationaleDialog(R.string.permission_camera_rationale, request)
    }

    private fun showRationaleDialog(@StringRes messageResId: Int, request: PermissionRequest) {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.allow) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.deny) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(messageResId)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}