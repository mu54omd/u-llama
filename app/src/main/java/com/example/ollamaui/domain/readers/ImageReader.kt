package com.example.ollamaui.domain.readers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ImageReader: DocumentReader() {
    @OptIn(ExperimentalEncodingApi::class)
    override fun readFromInputStream(inputStream: InputStream): Pair<String?, String?> {
        val bitmap = BitmapFactory.decodeStream(inputStream)
        var result: String?
        try {
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true) // Smaller size
            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream) // Higher compression
            val byteArray = byteArrayOutputStream.toByteArray()
            result = Base64.encode(byteArray)
            return Pair(result, null)
        }catch (e: IOException){
            result = e.printStackTrace().toString()
            return Pair(null, result)
        }
    }
}