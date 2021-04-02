package com.example.sobercheck.utils

import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.SparseArray
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import java.io.ByteArrayOutputStream


internal class CustomFaceDetector(delegate: Detector<Face>) : Detector<Face?>() {
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
        val jpegArray = byteArrayOutputStream.toByteArray()
        val tmpBitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.size)

        return delegate.detect(frame)
    }


    override fun isOperational(): Boolean {
        return delegate.isOperational
    }

    override fun setFocus(id: Int): Boolean {
        return delegate.setFocus(id)
    }

    companion object {
        private const val TAG = "CustomFaceDetector"
    }
}