package com.mu54omd.ullama.domain.readers

import java.io.InputStream

abstract class DocumentReader {
    abstract fun readFromInputStream(inputStream: InputStream, process: (Int) -> Unit):Pair<String?,String?>
}