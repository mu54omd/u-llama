package com.example.ollamaui.domain.readers

import java.io.InputStream

abstract class DocumentReader {
    abstract fun readFromInputStream(inputStream: InputStream, process: (Int) -> Unit):Pair<String?,String?>
}