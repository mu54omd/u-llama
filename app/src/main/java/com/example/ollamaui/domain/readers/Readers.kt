package com.example.ollamaui.domain.readers

class Readers {
    companion object{
        fun getReaderForDocType(documentType: String): DocumentReader{
            return when(documentType){
                DocumentType.PDF.type -> PDFReader()
                DocumentType.PlainText.type -> PlainTextReader()
                DocumentType.MarkDown.type -> PlainTextReader()
                DocumentType.Docx.type -> DocxReader()
                DocumentType.PNG.type -> ImageReader()
                DocumentType.JPG.type -> ImageReader()
                DocumentType.JPEG.type -> ImageReader()
                else -> UnsupportedReader()
            }
        }
    }
}