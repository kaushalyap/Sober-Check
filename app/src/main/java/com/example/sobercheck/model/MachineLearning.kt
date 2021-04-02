package com.example.sobercheck.model

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MachineLearning {

    private lateinit var interpreter: Interpreter
    private val labels: Array<String> = arrayOf("sober", "drunk")

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

    fun predictFromSelfie(selfie: Bitmap): Boolean {
        val bitmap = Bitmap.createScaledBitmap(selfie, 224, 224, true)
        val input = ByteBuffer.allocateDirect(224 * 224 * 3 * 4).order(ByteOrder.nativeOrder())
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val px = bitmap.getPixel(x, y)

                val r = Color.red(px)
                val g = Color.green(px)
                val b = Color.blue(px)

                val rf = (r - 127) / 255f
                val gf = (g - 127) / 255f
                val bf = (b - 127) / 255f

                input.putFloat(rf)
                input.putFloat(gf)
                input.putFloat(bf)
            }
        }

        val bufferSize = 1000 * java.lang.Float.SIZE / java.lang.Byte.SIZE
        val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
        interpreter.run(input, modelOutput)

        var label = ""
        var newLabel = ""
        var probability = 0f
        var highestProbability = 0f

        modelOutput.rewind()
        val results = modelOutput.asFloatBuffer()

        for (i in results.capacity()) {
            label = labels[i]
            probability = results.get(i)

            if (probability > highestProbability) {
                highestProbability = probability
                newLabel = label
            }
        }
        return newLabel != "sober"
    }

    fun predictFromAccelerometer(acceleration: ArrayList<AccelerationPoint>): Boolean {

        val x = FloatArray(10)
        val y = FloatArray(10)
        val z = FloatArray(10)

        var count = 0
        for (point in acceleration) {
            x[count] = point.x
            y[count] = point.y
            z[count] = point.z
            count += 1
        }

        val input: Array<FloatArray> = arrayOf(x, y, z)

        val bufferSize = 1000 * java.lang.Float.SIZE / java.lang.Byte.SIZE
        val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
        interpreter.run(input, modelOutput)

        var label = ""
        var newLabel = ""
        var probability = 0f
        var highestProbability = 0f

        modelOutput.rewind()
        val results = modelOutput.asFloatBuffer()

        for (i in results.capacity()) {
            label = labels[i]
            probability = results.get(i)

            if (probability > highestProbability) {
                highestProbability = probability
                newLabel = label
            }
        }
        return newLabel != "sober"
    }

    companion object {
        private const val TAG = "MachineLearning"
        private const val SELFIE_MODEL = "selfie"
        private const val ACCELERATOR_MODEL = "accel"
    }
}