package com.example.ollamaui.domain.readers

import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.IOException
import java.io.InputStream

class DocxReader: DocumentReader() {
    override fun readFromInputStream(inputStream: InputStream, process: (Int) -> Unit): Pair<String?, String?> {
        try {
            val document = XWPFDocument(inputStream)
            val paragraphs = document.paragraphs
            val result = StringBuilder()
            for(i in 0..paragraphs.size -1){
                process(((i.toFloat()/paragraphs.size.toFloat())*100).toInt())
                result.append(" ").append(paragraphs[i].text)
            }
            return Pair(result.toString(), null)
        }catch (e: IOException){
            return Pair(null, e.printStackTrace().toString())
        }catch (e: Exception){
            return Pair(null, e.printStackTrace().toString())
        }
    }
}