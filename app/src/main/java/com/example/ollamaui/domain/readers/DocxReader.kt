package com.example.ollamaui.domain.readers

import java.io.InputStream

class DocxReader: DocumentReader() {
    override fun readFromInputStream(inputStream: InputStream): Pair<String?, String?> {
        return Pair(null, "Not Implemented yet")
    }
}