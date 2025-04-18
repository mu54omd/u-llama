package com.mu54omd.ullama.domain.readers

import com.mu54omd.ullama.domain.helper.removeEmptyLines
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
                result.append("\n").append(paragraphs[i].text.removeEmptyLines())
            }
            return Pair(result.toString(), null)
        }catch (e: IOException){
            return Pair(null, e.printStackTrace().toString())
        }catch (e: Exception){
            return Pair(null, e.printStackTrace().toString())
        }
    }
}