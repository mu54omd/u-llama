package com.example.ollamaui.domain.readers

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.IOException
import java.io.InputStream

class PDFReader:DocumentReader() {
    override fun readFromInputStream(inputStream: InputStream): Pair<String?, String?> {
        val pdfReader = PdfReader(inputStream)
        var result = ""
        try {
            for (i in 1..pdfReader.numberOfPages) {
                result += "\n" + PdfTextExtractor.getTextFromPage(pdfReader, i)
            }
            return Pair(result, null)
        }catch (e: IOException){
            result = e.printStackTrace().toString()
            return Pair(null, result)
        }
    }
}