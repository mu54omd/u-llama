package com.example.ollamaui.ui.common

import android.graphics.Bitmap
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun bitmapToBase64(bitmap: Bitmap): String{
    var width: Float
    var height: Float
    val widthRatio: Float = bitmap.width / 400f
    val heightRatio: Float = bitmap.height / 400f
    if(widthRatio >= heightRatio){
        width = 400f
        height = (width/bitmap.width)*bitmap.height
    }else{
        height = 400f
        width = (height/bitmap.height)*bitmap.width
    }
    val resizedBitmap = bitmap.scale(width = width.toInt(), height = height.toInt()) // Smaller size
    val byteArrayOutputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream) // Higher compression
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encode(byteArray)
}