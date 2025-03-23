package com.example.ollamaui.domain.readers

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.IOException
import java.io.InputStream

class PDFReader:DocumentReader() {
    override fun readFromInputStream(inputStream: InputStream, process: (Int) -> Unit): Pair<String?, String?> {
        val pdfReader = PdfReader(inputStream)
        val pdfPages = pdfReader.numberOfPages
        var result = ""
        try {
            for (i in 1..pdfPages) {
                process(((i.toFloat()/pdfPages.toFloat())*100).toInt())
                result += "\n" + PdfTextExtractor.getTextFromPage(pdfReader, i)
            }
            return Pair(result, null)
        }catch (e: IOException){
            result = e.printStackTrace().toString()
            return Pair(null, result)
        }
    }
}