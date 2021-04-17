package com.example.sobercheck.model

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.coroutines.CompletableDeferred
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File


class MachineLearning {

    private lateinit var modelFile: File
    private lateinit var interpreter: Interpreter
    private lateinit var inputImageBuffer: TensorImage

    fun downloadModels() {
        downloadSelfieModel()
        downloadAccelerometerModel()
    }

    private fun downloadSelfieModel() {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(
                SELFIE_QUANTIZED_MODEL,
                DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions
            )
            .addOnSuccessListener {
                Log.d(TAG, "Selfie model downloaded successfully!")
            }
            .addOnFailureListener {
                Log.d(TAG, "Selfie model download failed! probably no internet")
            }
    }

    private fun downloadAccelerometerModel() {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel(ACCELERATOR_MODEL, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
            .addOnSuccessListener {
                Log.d(TAG, "Accelerator model downloaded successfully!")
            }
            .addOnFailureListener {
                Log.d(TAG, "Accelerator model download failed! probably no internet")
            }
    }

    suspend fun predictFromSelfie(selfie: Bitmap): Boolean {

        val deferred = CompletableDeferred<Boolean>()
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        FirebaseModelDownloader.getInstance()
            .getModel(
                SELFIE_QUANTIZED_MODEL,
                DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions
            )
            .addOnSuccessListener { customModel ->

                modelFile = (customModel.file ?: return@addOnSuccessListener)
                Log.d(TAG, modelFile.name)
                interpreter = Interpreter(modelFile)

                createInputTensor(selfie)

                val outputProbabilityBuffer =
                    TensorBuffer.createFixedSize(intArrayOf(1, 106, 106), DataType.FLOAT32)

                interpreter.run(inputImageBuffer.buffer, outputProbabilityBuffer.buffer)

                var isDrunk = false
                if (outputProbabilityBuffer.floatArray[0] >= .5) {
                    isDrunk = true
                    Log.d(
                        TAG,
                        "Drunk, probability : " + outputProbabilityBuffer.floatArray[0].toString()
                    )
                } else
                    Log.d(
                        TAG,
                        "Sober, probability : " + outputProbabilityBuffer.floatArray[0].toString()
                    )
                deferred.complete(isDrunk)
            }
            .addOnFailureListener {
                Log.d(TAG, it.message.toString())
            }

        return deferred.await()
    }

    private fun createInputTensor(selfie: Bitmap) {
        val imageDataType = interpreter.getInputTensor(0).dataType()
        inputImageBuffer = TensorImage(imageDataType)
        inputImageBuffer.load(selfie)
        inputImageBuffer = normalizeImage()
    }

    private fun normalizeImage(): TensorImage {
        val imageProcessor: ImageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(106, 106, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(127.5f, 127.5f))
            .build()

        return imageProcessor.process(inputImageBuffer)
    }

    suspend fun predictFromAccelerometer(acceleration: ArrayList<AccelerationPoint>): Boolean {
        val deferred = CompletableDeferred<Boolean>()


        return deferred.await()
    }

    companion object {
        private const val TAG = "MachineLearning"

        @Suppress("unused")
        private const val SELFIE_MODEL = "selfie"
        private const val SELFIE_QUANTIZED_MODEL = "selfie-quantized"
        private const val ACCELERATOR_MODEL = "accel"
    }
}