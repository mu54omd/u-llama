package com.example.ollamaui.data.local.objectbox

import com.example.ollamaui.domain.model.objectbox.Document
import com.example.ollamaui.domain.model.objectbox.Document_
import io.objectbox.kotlin.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DocumentDatabase @Inject constructor() {

    private val docsBox = ObjectBoxStore.store.boxFor(Document::class.java)

    fun addDocument(document: Document): Long {
        return docsBox.put(document)
    }

    fun removeDocument(docId: Long) {
        docsBox.remove(docId)
    }

    fun getDocsCount(): Long {
        return docsBox.count()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllDocuments(): Flow<MutableList<Document>> =
        docsBox.query(Document_.docId.notNull()).build().flow().flowOn(Dispatchers.IO)
}