package com.example.ollamaui.ui.common

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun bitmapToBase64(bitmap: Bitmap): String{
    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true) // Smaller size
    val byteArrayOutputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream) // Higher compression
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encode(byteArray)
}