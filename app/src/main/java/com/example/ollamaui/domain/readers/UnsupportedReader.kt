package com.example.ollamaui.domain.readers

import java.io.InputStream

class UnsupportedReader: DocumentReader() {
    override fun readFromInputStream(inputStream: InputStream): Pair<String?, String?> {
        return Pair(null, "The selected file is not supported yet!")
    }
}