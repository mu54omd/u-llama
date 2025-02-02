package com.example.ollamaui.data.local.objectbox

import com.example.ollamaui.domain.model.objectbox.File
import com.example.ollamaui.domain.model.objectbox.File_
import io.objectbox.kotlin.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FileDatabase @Inject constructor() {

    private val fileBox = ObjectBoxStore.store.boxFor(File::class.java)

    fun addFile(file: File): Long {
        return fileBox.put(file)
    }

    fun removeFile(fileId: Long) {
        fileBox.remove(fileId)
    }

    fun getFilesCount(): Long {
        return fileBox.count()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllFiles(): Flow<MutableList<File>> =
        fileBox.query(File_.fileId.notNull()).build().flow().flowOn(Dispatchers.IO)
}