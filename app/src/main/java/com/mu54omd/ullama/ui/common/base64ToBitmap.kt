package com.mu54omd.ullama.ui.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun base64ToBitmap(base64String: String): Bitmap{
    val decodedBytes = Base64.decode(base64String)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}