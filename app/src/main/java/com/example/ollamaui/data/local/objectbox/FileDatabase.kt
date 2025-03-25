package com.example.ollamaui.data.local.objectbox

import com.example.ollamaui.domain.model.objectbox.File
import com.example.ollamaui.domain.model.objectbox.File_
import com.example.ollamaui.domain.model.objectbox.StableFile
import com.example.ollamaui.domain.model.objectbox.toStableFile
import io.objectbox.kotlin.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FileDatabase @Inject constructor() {

    private val fileBox = ObjectBoxStore.store.boxFor(File::class.java)

    fun addFile(file: File): Long {
        return fileBox.put(file)
    }

    fun removeFile(fileId: Long) {
        fileBox.remove(fileId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllFiles(): Flow<List<StableFile>> =
        fileBox.query(File_.fileId.notNull())
            .build()
            .flow()
            .map { filesList -> filesList.map { it.toStableFile() } }
            .flowOn(Dispatchers.IO)
}