package com.example.sobercheck.utils

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import android.util.SparseArray
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import java.io.ByteArrayOutputStream

internal class CustomFaceDetector(delegate: Detector<Face>) : Detector<Face?>() {
    private lateinit var faceBitmap: Bitmap
    private val delegate: Detector<Face> = delegate

    override fun detect(frame: Frame): SparseArray<Face?> {
        val yuvImage = YuvImage(
            frame.grayscaleImageData.array(),
            ImageFormat.NV21,
            frame.metadata.width,
            frame.metadata.height,
            null
        )
        val byteArrayOutputStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            Rect(0, 0, frame.metadata.width, frame.metadata.height),
            100,
            byteArrayOutputStream
        )

        val faces = delegate.detect(frame)
        val conf = Bitmap.Config.ARGB_8888
        faceBitmap = Bitmap.createBitmap(100, 100, conf)

        if (faces.size() > 0) {
            val face: Face? = faces.valueAt(0)
            val startX: Float? = face?.position?.x
            val startY: Float? = face?.position?.y
            faces.clear()
            faceBitmap = Bitmap.createBitmap(
                faceBitmap,
                startX as Int, startY as Int,
                face.width as Int,
                face.height as Int
            )
            Log.d("CustomFaceDetector", faceBitmap.byteCount.toString())
        }
        return faces
    }


    override fun isOperational(): Boolean {
        return delegate.isOperational
    }

    override fun setFocus(id: Int): Boolean {
        return delegate.setFocus(id)
    }
}