package com.example.sobercheck.model

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter

class MachineLearning {

    private lateinit var interpreter: Interpreter
    fun downloadModels() {
        downloadSelfieModel()
        downloadAccelerometerModel()
    }

    private fun downloadSelfieModel() {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(SELFIE_MODEL, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
            .addOnCompleteListener { customModel ->

                customModel.addOnSuccessListener { model: CustomModel? ->
                    val modelFile = model?.file
                    if (modelFile != null) {
                        interpreter = Interpreter(modelFile)
                    }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Internet is necessary to download the model!")
            }
    }

    private fun downloadAccelerometerModel() {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(ACCELERATOR_MODEL, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
            .addOnCompleteListener { customModel ->
                customModel.addOnSuccessListener { model: CustomModel? ->
                    val modelFile = model?.file
                    if (modelFile != null) {
                        interpreter = Interpreter(modelFile)
                    }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Internet is necessary to download the model!")
            }
    }

    fun predict(bitmap: Bitmap): Boolean {
        return predictFromSelfie(bitmap) && predictFromAccelerometer()
    }

    fun predictFromSelfie(selfie: Bitmap): Boolean {


        return false
    }

    fun predictFromAccelerometer(): Boolean {

        return false
    }

    companion object {
        private const val TAG = "MachineLearning"
        private const val SELFIE_MODEL = "selfie"
        private const val ACCELERATOR_MODEL = "accel"
    }
}