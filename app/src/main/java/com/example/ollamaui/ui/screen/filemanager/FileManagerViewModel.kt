package com.example.ollamaui.ui.screen.filemanager

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.data.local.objectbox.ChunkDatabase
import com.example.ollamaui.data.local.objectbox.FileDatabase
import com.example.ollamaui.domain.helper.Splitter
import com.example.ollamaui.domain.model.LogModel
import com.example.ollamaui.domain.model.embed.EmbedInputModel
import com.example.ollamaui.domain.model.objectbox.Chunk
import com.example.ollamaui.domain.model.objectbox.File
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.ui.screen.chat.AttachedFilesList
import com.example.ollamaui.utils.Constants.OLLAMA_EMBED_ENDPOINT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FileManagerViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
    private val fileDatabase: FileDatabase,
    private val chunkDatabase: ChunkDatabase,
): ViewModel() {
    private val _attachedFiles = fileDatabase.getAllFiles().map { AttachedFilesList(item = it) }
    val attachedFiles = _attachedFiles
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AttachedFilesList()
        )

    private val _embeddingInProgressList = MutableStateFlow(emptyList<Long>())
    val embeddingInProgressList = _embeddingInProgressList.asStateFlow()

    fun attachFileToChat(
        attachResult: String?,
        attachError: String?,
        embeddingModel: String,
        fileName: String,
        documentType: String,
        hash: String,
        ollamaBaseAddress: String,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val isImage = documentType in listOf("png", "jpg", "jpeg")
                attachResult?.let { result ->
                    val filteredList = attachedFiles.value.item.filter {
                        (it.attachResult == attachResult)
                                && (it.fileName == fileName)
                                && (it.fileType == documentType)
                                && (it.hash == hash)
                    }
                    if (filteredList.isEmpty()) {
                        val fileId = fileDatabase.addFile(
                            file = File(
                                attachResult = result,
                                fileName = fileName,
                                fileType = documentType,
                                fileAddedTime = System.currentTimeMillis(),
                                hash = hash,
                                isImage = isImage
                            )
                        )
                        if (!isImage) {
                            val chunks = Splitter.createChunks(
                                docText = result,
                                chunkSize = 100,
                                chunkOverlap = 5
                            )
                            ollamaPostEmbed(
                                text = chunks,
                                embeddingModel = embeddingModel,
                                docId = fileId,
                                fileName = fileName,
                                ollamaBaseAddress = ollamaBaseAddress
                            )
                        }
                    }
                }
                when {
                    attachResult != null -> ollamaRepository.insertLogToDb(
                        LogModel(
                            date = LocalDateTime.now().toString(),
                            type = "SUCCESS",
                            content = "attach-file: $fileName",
                        )
                    )

                    attachError != null -> ollamaRepository.insertLogToDb(
                        LogModel(
                            date = LocalDateTime.now().toString(),
                            type = "ERROR",
                            content = "attach-file: $fileName - $attachError",
                        )
                    )
                }
            }
        }
    }

    fun removeAttachedFile(fileId: Long, isImage: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (!isImage) {
                    fileDatabase.removeFile(fileId = fileId)
                    chunkDatabase.removeChunk(
                        docId = fileId,
                        onProgress = { deleted, total -> Log.d("cTAG", "$deleted/$total removed") }
                            )
                } else {
                    fileDatabase.removeFile(fileId = fileId)
                }
            }
        }
    }

    private fun ollamaPostEmbed(text: List<String>, embeddingModel: String, docId: Long, fileName: String, ollamaBaseAddress: String){
        viewModelScope.launch {
            _embeddingInProgressList.update { it + docId }
            withContext(Dispatchers.IO) {
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "START",
                        content = "ollama post embed: ${ollamaBaseAddress}${OLLAMA_EMBED_ENDPOINT}",
                    )
                )
                ollamaRepository.postOllamaEmbed(
                    baseUrl = ollamaBaseAddress,
                    embedEndpoint = OLLAMA_EMBED_ENDPOINT,
                    embedInputModel = EmbedInputModel(
                        model = embeddingModel,
                        input = text
                    )
                )
                    .onRight { response ->
                        response.embeddings.forEachIndexed { index, chunkedEmbedding ->
                            chunkDatabase.addChunk(
                                chunk = Chunk(
                                    docId = docId,
                                    docFileName = fileName,
                                    chunkData = text[index],
                                    chunkEmbedding = chunkedEmbedding,
                                )
                            )
                        }
                        ollamaRepository.insertLogToDb(
                            LogModel(
                                date = LocalDateTime.now().toString(),
                                type = "SUCCESS",
                                content = "ollama post embed: ${ollamaBaseAddress}${OLLAMA_EMBED_ENDPOINT}",
                            )
                        )
                        _embeddingInProgressList.update { it - docId }
                    }
                    .onLeft { error ->
                        ollamaRepository.insertLogToDb(
                            LogModel(
                                date = LocalDateTime.now().toString(),
                                type = "ERROR",
                                content = "ollama post embed:  ${error.t.message}",
                            )
                        )
                    }
            }
        }
    }

}