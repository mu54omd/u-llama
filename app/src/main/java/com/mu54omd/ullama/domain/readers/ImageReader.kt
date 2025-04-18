package com.mu54omd.ullama.domain.readers

import android.graphics.BitmapFactory
import com.mu54omd.ullama.ui.common.bitmapToBase64
import java.io.InputStream

class ImageReader: DocumentReader() {
    override fun readFromInputStream(inputStream: InputStream, process: (Int) -> Unit): Pair<String?, String?> {
        val bitmap = BitmapFactory.decodeStream(inputStream)
        var result: String?
        try {
            result = bitmapToBase64(bitmap)
            return Pair(result, null)
        }catch (e: Exception){
            result = e.printStackTrace().toString()
            return Pair(null, result)
        }
    }
}