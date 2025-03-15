package com.example.ollamaui.domain.readers

import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.IOException
import java.io.InputStream

class DocxReader: DocumentReader() {
    override fun readFromInputStream(inputStream: InputStream): Pair<String?, String?> {
        try {
            val document = XWPFDocument(inputStream)
            val paragraphs = document.paragraphs
            val result = StringBuilder()
            paragraphs.forEach { result.append(" ").append(it.text) }
            return Pair(result.toString(), null)
        }catch (e: IOException){
            return Pair(null, e.printStackTrace().toString())
        }catch (e: Exception){
            return Pair(null, e.printStackTrace().toString())
        }
    }
}