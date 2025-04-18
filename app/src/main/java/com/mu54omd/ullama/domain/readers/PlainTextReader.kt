package com.mu54omd.ullama.domain.readers

import com.mu54omd.ullama.domain.helper.removeEmptyLines
import java.io.IOException
import java.io.InputStream

class PlainTextReader: DocumentReader() {
    override fun readFromInputStream(inputStream: InputStream, process: (Int) -> Unit): Pair<String?,String?> {
        val size: Int
        var result: String
        try {
            size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            result = String(buffer).removeEmptyLines()
            return Pair(result, null)
        } catch (e: IOException){
            result = e.printStackTrace().toString()
            return Pair(null, result)
        }
    }
}