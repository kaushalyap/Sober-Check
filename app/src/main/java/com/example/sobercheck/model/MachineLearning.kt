package com.example.sobercheck.model

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.coroutines.CompletableDeferred
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set
import kotlin.math.abs


class MachineLearning {

    private lateinit var modelFile: File
    private lateinit var selfieInterpreter: Interpreter
    private lateinit var accelInterpreter: Interpreter

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
//                Log.d(TAG, modelFile.name)
                selfieInterpreter = Interpreter(modelFile)

                createInputImageTensor(selfie)
                val outputProbabilityBuffer =
                    TensorBuffer.createFixedSize(intArrayOf(1), DataType.FLOAT32)

                selfieInterpreter.run(inputImageBuffer.buffer, outputProbabilityBuffer.buffer)

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
                Log.d(TAG, "Failure : ${it.message.toString()}")
            }

        return deferred.await()
    }

    private fun createAccelInputTensors() {
//        val xMeanInputType = interpreter.getInputTensor(0).dataType()
//        val xMedianInputType = interpreter.getInputTensor(1).dataType()
//        val xStdevInputType = interpreter.getInputTensor(2).dataType()
//        val xRawMinInputType = interpreter.getInputTensor(3).dataType()
//        val xRawMaxInputType = interpreter.getInputTensor(4).dataType()
//        val xAbsMinInputType = interpreter.getInputTensor(5).dataType()
//        val xAbsMaxInputType = interpreter.getInputTensor(6).dataType()
//
//        val yMeanInputType = interpreter.getInputTensor(7).dataType()
//        val yMedianInputType = interpreter.getInputTensor(8).dataType()
//        val yStdevInputType = interpreter.getInputTensor(9).dataType()
//        val yRawMinInputType = interpreter.getInputTensor(10).dataType()
//        val yRawMaxInputType = interpreter.getInputTensor(11).dataType()
//        val yAbsMinInputType = interpreter.getInputTensor(12).dataType()
//        val yAbsMaxInputType = interpreter.getInputTensor(13).dataType()
//
//        val zMeanInputType = interpreter.getInputTensor(14).dataType()
//        val zMedianInputType = interpreter.getInputTensor(15).dataType()
//        val zStdevInputType = interpreter.getInputTensor(16).dataType()
//        val zRawMinInputType = interpreter.getInputTensor(17).dataType()
//        val zRawMaxInputType = interpreter.getInputTensor(18).dataType()
//        val zAbsMinInputType = interpreter.getInputTensor(19).dataType()
//        val zAbsMaxInputType = interpreter.getInputTensor(20).dataType()
//
    }

    private fun createInputImageTensor(selfie: Bitmap) {
        val imageDataType = selfieInterpreter.getInputTensor(0).dataType()
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

        Log.d(TAG, "Number of points : ${acceleration.size}")

        val deferred = CompletableDeferred<Boolean>()
        val accelInputs = generateAccelFeatureInputs(acceleration)

        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        FirebaseModelDownloader.getInstance()
            .getModel(
                ACCELERATOR_MODEL,
                DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions
            )
            .addOnSuccessListener { customModel ->

                modelFile = (customModel.file ?: return@addOnSuccessListener)
//                Log.d(TAG, modelFile.name)
                accelInterpreter = Interpreter(modelFile)

                val inputs = arrayOf<Any>(
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
                )

                for (i in 0..20) {
                    inputs[i] = accelInputs[i]
                }

                val output = Array<FloatArray>(1) { FloatArray(1) }
                val outputs: MutableMap<Int, Any> = HashMap()
                outputs[0] = output

                accelInterpreter.runForMultipleInputsOutputs(inputs, outputs)


                var isDrunk = false
                if (output[0][0] >= .5) {
                    isDrunk = true
                    Log.d(
                        TAG,
                        "Drunk, probability : " + output[0][0].toString()
                    )
                } else
                    Log.d(
                        TAG,
                        "Sober, probability : " + output[0][0].toString()
                    )
                deferred.complete(isDrunk)
            }
            .addOnFailureListener {
                Log.d(TAG, "Failure : ${it.message.toString()}")
            }
        return deferred.await()
    }

    private fun generateAccelFeatureInputs(acceleration: ArrayList<AccelerationPoint>): FloatArray {
        val descStatsX = DescriptiveStatistics()
        val descStatsY = DescriptiveStatistics()
        val descStatsZ = DescriptiveStatistics()

        val xValues = ArrayList<Float>()
        val yValues = ArrayList<Float>()
        val zValues = ArrayList<Float>()

        for (point in acceleration) {
            descStatsX.addValue(point.x.toDouble())
            descStatsY.addValue(point.y.toDouble())
            descStatsZ.addValue(point.z.toDouble())
            xValues.add(abs(point.x))
            yValues.add(abs(point.y))
            zValues.add(abs(point.z))
        }

        val xMean = descStatsX.max.toFloat()
        val xMedian = descStatsX.getPercentile(FIFTYTH_PERCENTILE).toFloat()
        val xStdev = descStatsX.standardDeviation.toFloat()
        val xRawMin = descStatsX.min.toFloat()
        val xRawMax = descStatsX.max.toFloat()
        val xAbsMin = xValues.minOrNull()!!
        val xAbsMax = xValues.maxOrNull()!!

        val yMean = descStatsY.mean.toFloat()
        val yMedian = descStatsX.getPercentile(FIFTYTH_PERCENTILE).toFloat()
        val yStdev = descStatsY.standardDeviation.toFloat()
        val yRawMin = descStatsY.min.toFloat()
        val yRawMax = descStatsY.max.toFloat()
        val yAbsMin = yValues.minOrNull()!!
        val yAbsMax = yValues.maxOrNull()!!

        val zMean = descStatsZ.mean.toFloat()
        val zMedian = descStatsX.getPercentile(FIFTYTH_PERCENTILE).toFloat()
        val zStdev = descStatsZ.standardDeviation.toFloat()
        val zRawMin = descStatsZ.min.toFloat()
        val zRawMax = descStatsZ.max.toFloat()
        val zAbsMin = zValues.minOrNull()!!
        val zAbsMax = zValues.maxOrNull()!!

        val inputs = floatArrayOf(
            xMean,
            xMedian,
            xStdev,
            xRawMin,
            xRawMax,
            xAbsMin,
            xAbsMax,
            yMean,
            yMedian,
            yStdev,
            yRawMin,
            yRawMax,
            yAbsMin,
            yAbsMax,
            zMean,
            zMedian,
            zStdev,
            zRawMin,
            zRawMax,
            zAbsMin,
            zAbsMax
        )


        for (input in inputs) {
            Log.d(TAG, "$input")
        }

        return inputs
    }

    companion object {
        private const val TAG = "MachineLearning"

        @Suppress("unused")
        private const val SELFIE_MODEL = "selfie"
        private const val SELFIE_QUANTIZED_MODEL = "selfie-quantized"
        private const val ACCELERATOR_MODEL = "accel-k"

        @Suppress("unused")
        private const val ACCELERATOR_QUANTIZED_MODEL = "accel-k-quantized"
        private const val FIFTYTH_PERCENTILE = 50.0
    }
}