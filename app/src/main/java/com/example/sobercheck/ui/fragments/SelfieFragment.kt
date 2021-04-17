package com.example.sobercheck.ui.fragments

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentSelfieBinding
import com.example.sobercheck.model.MachineLearning
import com.example.sobercheck.utils.FaceContourDetectionProcessor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class SelfieFragment : Fragment() {

    private var _binding: FragmentSelfieBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraPermissionsRequester: PermissionsRequester
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var selfie: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelfieBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.btnCamera.setOnClickListener {
            lifecycleScope.launch {
                takePhoto()
                val prediction = MachineLearning().predictFromSelfie(selfie)
                val action =
                    SelfieFragmentDirections.actionSelfieToWalkingExercise(prediction)
                findNavController().navigate(action)
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraPermissionsRequester.launch()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        selectAnalyzer()
                    )
                }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun selectAnalyzer(): ImageAnalysis.Analyzer {
        return FaceContourDetectionProcessor(binding.graphicOverlay)
    }

    private suspend fun takePhoto(): Boolean {
        val deferred = CompletableDeferred<Boolean>()
        val imageCapture = imageCapture
        imageCapture?.takePicture(cameraExecutor, object :
            ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                selfie = imageProxyToBitmap(image)
                deferred.complete(true)
            }
        })
        Toast.makeText(context, "Selfie taken!", Toast.LENGTH_SHORT).show()
        return deferred.await()
    }

    internal fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cameraPermissionsRequester = constructPermissionsRequest(
            Manifest.permission.CAMERA,
            onShowRationale = ::onCameraShowRationale,
            onPermissionDenied = ::onCameraDenied,
            onNeverAskAgain = ::onCameraNeverAskAgain,
            requiresPermission = ::startCamera
        )
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
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "SELFIE"
    }
}