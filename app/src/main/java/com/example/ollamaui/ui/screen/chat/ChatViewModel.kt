package com.example.ollamaui.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.data.local.objectbox.ChunkDatabase
import com.example.ollamaui.data.local.objectbox.FileDatabase
import com.example.ollamaui.domain.model.ApiError
import com.example.ollamaui.domain.model.LogModel
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.domain.model.chat.ChatInputModel
import com.example.ollamaui.domain.model.chat.ChatModel
import com.example.ollamaui.domain.model.chat.EmptyChatModel
import com.example.ollamaui.domain.model.chat.EmptyChatResponse
import com.example.ollamaui.domain.model.chat.ModelParameters
import com.example.ollamaui.domain.model.embed.EmbedInputModel
import com.example.ollamaui.domain.model.objectbox.Chunk
import com.example.ollamaui.domain.model.objectbox.File
import com.example.ollamaui.domain.objectbox.Splitter
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.utils.Constants.OLLAMA_CHAT_ENDPOINT
import com.example.ollamaui.utils.Constants.OLLAMA_EMBED_ENDPOINT
import com.example.ollamaui.utils.Constants.USER_ROLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
class ChatViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
    private val chunkDatabase: ChunkDatabase,
    private val fileDatabase: FileDatabase,
):ViewModel() {

    private val _chatState = MutableStateFlow(ChatStates())
    val chatState = _chatState.asStateFlow()

    private val _attachedFiles = fileDatabase.getAllFiles().map { file -> AttachedFilesList(item = file) }
    val attachedFiles = _attachedFiles
        .stateIn(
            scope = viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = AttachedFilesList()
        )

    private val isRespondingList = mutableListOf<Int>()
    private val jobs = mutableMapOf<Int, Job>()

    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    fun sendButton(text: String, selectedFiles: List<File>, embeddingModel: String){
        val messages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        val oldChatModel = chatState.value.chatModel
        val imageList = selectedFiles.filter{ it.isImage }.map { it.attachResult }
        when{
           selectedFiles.isEmpty() -> {
                messages.add(MessageModel(content = text.trim(), role = USER_ROLE))
                _chatState.update {
                    it.copy(
                        chatModel = ChatModel(
                            chatId = oldChatModel.chatId,
                            chatTitle = oldChatModel.chatTitle,
                            chatMessages = MessagesModel(messageModels = messages),
                            modelName = oldChatModel.modelName,
                        ),
                        isDatabaseChanged = true
                    )
                }
                ollamaPostMessage(
                    messages = MessagesModel(messageModels = messages),
                    chatId = oldChatModel.chatId
                )
            }
            imageList.isNotEmpty() -> {
                messages.add(MessageModel(content = text.trim(), role = USER_ROLE, images = imageList))
                _chatState.update {
                    it.copy(
                        chatModel = ChatModel(
                            chatId = oldChatModel.chatId,
                            chatTitle = oldChatModel.chatTitle,
                            chatMessages = MessagesModel(messageModels = messages),
                            modelName = oldChatModel.modelName,
                        ),
                        isDatabaseChanged = true
                    )
                }
                ollamaPostMessage(
                    messages = MessagesModel(messageModels = messages),
                    chatId = oldChatModel.chatId
                )
            }
            else -> {
                val job =
                    getSimilarChunk(
                        fileIds = selectedFiles.filter { !it.isImage }.map{ it.fileId },
                        query = text.trim(),
                        embeddingModel = if (embeddingModel != "") embeddingModel else chatState.value.chatModel.modelName,
                    )
                job.invokeOnCompletion {
                    messages.add(
                        MessageModel(
                            content = "Using this data: {${chatState.value.retrievedContext.trimIndent()}}. Respond to this prompt: {${text.trimIndent()}}.",
                            role = USER_ROLE,
                        )
                    )
                    _chatState.update {
                        it.copy(
                            chatModel = ChatModel(
                                chatId = oldChatModel.chatId,
                                chatTitle = oldChatModel.chatTitle,
                                chatMessages = MessagesModel(messageModels = messages),
                                modelName = oldChatModel.modelName,
                            ),
                            isDatabaseChanged = true
                        )
                    }
                    ollamaPostMessage(
                        messages = MessagesModel(messageModels = messages),
                        chatId = oldChatModel.chatId
                    )
                }
            }
        }

    }
    fun retry(){
        if(!isRespondingList.contains(chatState.value.chatModel.chatId)) {
            isRespondingList.add(chatState.value.chatModel.chatId)
        }
        _chatState.update { it.copy(chatError = null, isRespondingList = isRespondingList, isSendingFailed = false) }
        ollamaPostMessage(messages = chatState.value.chatModel.chatMessages, chatId = chatState.value.chatModel.chatId)
    }
    fun stop(chatId: Int){
        jobs[chatId]?.cancel()
        jobs.remove(chatId)
        isRespondingList.remove(chatId)
        _chatState.update { it.copy(
            isSendingFailed = true,
            isRespondingList = isRespondingList,
            isDatabaseChanged = true,
            chatError = NetworkError(error = ApiError.UnknownError, t = Throwable("The request has been cancelled by user")),
            chatModel = chatState.value.chatModel.copy(newMessageStatus = 2)
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                killOllama()
            }
        }
    }

    fun removeLastDialogFromDatabase(){
        val messages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        messages.removeAt(messages.lastIndex)
        _chatState.update {
            it.copy(
                chatModel = chatState.value.chatModel.copy(chatMessages = MessagesModel(messageModels = messages), newMessageStatus = 0),
                isDatabaseChanged = true,
                isSendingFailed = false,
                chatError = null
            )
        }
    }

    fun loadStates(chatModel: ChatModel, url: String, modelParameters: ModelParameters) {
        _chatState.update {
            it.copy(
                chatModel = chatModel,
                ollamaBaseAddress = url,
                isSendingFailed = chatModel.newMessageStatus == 2,
                chatOptions = modelParameters
            )
        }
        viewModelScope.launch {
            if(chatModel.newMessageStatus !=2) {
                withContext(Dispatchers.IO) {
                    uploadChatToDatabase(chatModel = chatModel.copy(newMessageStatus = 0))
                }
            }
        }
    }

    fun clearStates(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (chatState.value.isDatabaseChanged && chatState.value.chatModel.newMessageStatus != 2) {
                    uploadChatToDatabase(chatModel = chatState.value.chatModel.copy(newMessageStatus = 0))
                } else if (chatState.value.isDatabaseChanged && chatState.value.chatModel.newMessageStatus == 2) {
                    uploadChatToDatabase(chatModel = chatState.value.chatModel)
                }
                _chatState.update {
                    it.copy(
                        chatModel = EmptyChatModel.empty,
                        chatResponse = EmptyChatResponse.empty,
                        chatError = null,
                        isDatabaseChanged = false
                    )
                }
            }
        }
    }

    fun attachFileToChat(
        attachResult: String?,
        attachError: String?,
        embeddingModel: String,
        fileName: String,
        documentType: String,
        hash: String,
        isEmbeddingModelSet: Boolean
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val isImage = documentType in listOf("png", "jpg", "jpeg")
                attachResult?.let { result ->
                    val filteredList = attachedFiles.value.item.filter {
                        (it.attachResult == attachResult)
                                && (it.fileName == fileName)
                                && (it.fileType == documentType)
                                && (it.chatId == chatState.value.chatModel.chatId)
                                && (it.hash == hash)
                    }
                    if (filteredList.isEmpty()) {
                        val fileId = fileDatabase.addFile(
                            file = File(
                                attachResult = result,
                                fileName = fileName,
                                fileType = documentType,
                                fileAddedTime = System.currentTimeMillis(),
                                chatId = chatState.value.chatModel.chatId,
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
                                embeddingModel = if (isEmbeddingModelSet) embeddingModel else chatState.value.chatModel.modelName,
                                docId = fileId,
                                fileName = fileName
                            )
                        }
                    }
                }
                when {
                    attachResult != null -> ollamaRepository.insertLogToDb(
                        LogModel(
                            date = LocalDateTime.now().toString(),
                            type = "attach-file",
                            content = "Result: Success - $fileName",
                        )
                    )

                    attachError != null -> ollamaRepository.insertLogToDb(
                        LogModel(
                            date = LocalDateTime.now().toString(),
                            type = "attach-file",
                            content = "Result: Failed - $fileName - $attachError",
                        )
                    )
                }
            }
        }
    }

    fun removeAttachedFile(index: Int, isImage: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (!isImage) {
                    attachedFiles.value.item[index].fileId.let {
                        fileDatabase.removeFile(fileId = it)
                        chunkDatabase.removeChunk(docId = it)

                    }
                } else {
                    attachedFiles.value.item[index].fileId.let {
                        fileDatabase.removeFile(fileId = it)
                    }
                }
            }
        }
    }
    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/

    private fun ollamaPostMessage(messages: MessagesModel, chatId: Int){
        val oldMessages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        val oldChatModel = chatState.value.chatModel
        val job = jobs[chatId] ?: Job()
        jobs[chatId] = job
        viewModelScope.launch(job) {
            withContext(Dispatchers.IO) {
                if (!isRespondingList.contains(chatState.value.chatModel.chatId)) isRespondingList.add(
                    chatId
                )
                _chatState.update { it.copy(isRespondingList = isRespondingList) }
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "ollama-post",
                        content = "post: ${chatState.value.ollamaBaseAddress}${OLLAMA_CHAT_ENDPOINT}",
                    )
                )
                ollamaRepository.postOllamaChat(
                    baseUrl = chatState.value.ollamaBaseAddress,
                    chatEndpoint = OLLAMA_CHAT_ENDPOINT,
                    chatInputModel = ChatInputModel(
                        model = chatState.value.chatModel.modelName,
                        messages = messages.messageModels,
                        keepAlive = 3600,
                        stream = false,
                        options = chatState.value.chatOptions
                    )
                ).onRight { response ->
                    oldMessages.add(
                        MessageModel(
                            content = response.message.content,
                            role = response.message.role
                        )
                    )
                    isRespondingList.remove(chatId)
                    _chatState.update { it.copy(isRespondingList = isRespondingList) }
                    if (chatState.value.chatModel.chatId == oldChatModel.chatId) {
                        _chatState.update {
                            it.copy(
                                chatModel = ChatModel(
                                    chatId = oldChatModel.chatId,
                                    chatTitle = oldChatModel.chatTitle,
                                    chatMessages = MessagesModel(messageModels = oldMessages),
                                    modelName = oldChatModel.modelName,
                                ),
                                chatResponse = response,
                                chatError = null
                            )
                        }
                    }
                    uploadChatToDatabase(
                        chatModel = ChatModel(
                            chatId = oldChatModel.chatId,
                            chatTitle = oldChatModel.chatTitle,
                            chatMessages = MessagesModel(messageModels = oldMessages),
                            modelName = oldChatModel.modelName,
                            newMessageStatus = if (chatState.value.chatModel.chatId == oldChatModel.chatId) 0 else 1
                        ),
                    )
                    ollamaRepository.insertLogToDb(
                        LogModel(
                            date = LocalDateTime.now().toString(),
                            type = "ollama-post",
                            content = "Result: Success",
                        )
                    )
                }.onLeft { error ->
                    isRespondingList.remove(chatId)
                    _chatState.update {
                        it.copy(
                            isRespondingList = isRespondingList,
                            isSendingFailed = true,
                            isDatabaseChanged = true,
                        )
                    }
                    if (chatState.value.chatModel.chatId == oldChatModel.chatId) {
                        _chatState.update {
                            it.copy(
                                chatError = error,
                                chatResponse = EmptyChatResponse.empty,
                                chatModel = chatState.value.chatModel.copy(newMessageStatus = 2)
                            )
                        }
                    }
                    uploadChatToDatabase(oldChatModel.copy(newMessageStatus = 2))
                    ollamaRepository.insertLogToDb(
                        LogModel(
                            date = LocalDateTime.now().toString(),
                            type = "ollama-post",
                            content = "Result: Failed - ${error.error}",
                        )
                    )
                }
            }
        }
    }
    private fun ollamaPostEmbed(text: List<String>, embeddingModel: String, docId: Long, fileName: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "ollama-embed",
                        content = "post: ${chatState.value.ollamaBaseAddress}${OLLAMA_EMBED_ENDPOINT}",
                    )
                )
                ollamaRepository.postOllamaEmbed(
                    baseUrl = chatState.value.ollamaBaseAddress,
                    embedEndpoint = OLLAMA_EMBED_ENDPOINT,
                    embedInputModel = EmbedInputModel(
                        model = embeddingModel,
                        input = text
                    )
                )
                    .onRight { response ->
                        _chatState.update { it.copy(embedResponse = response) }
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
                                type = "ollama-embed",
                                content = "Result: Success",
                            )
                        )
                    }
                    .onLeft { error ->
                        _chatState.update { it.copy(embedError = error) }
                        ollamaRepository.insertLogToDb(
                            LogModel(
                                date = LocalDateTime.now().toString(),
                                type = "ollama-embed",
                                content = "Result: Failed - ${error.error}",
                            )
                        )
                    }
            }
        }
    }

    private fun getSimilarChunk(fileIds: List<Long>, query: String, embeddingModel: String):Job{
        val job = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _chatState.update { it.copy(isRetrievedContextReady = false) }
                ollamaRepository.postOllamaEmbed(
                    baseUrl = chatState.value.ollamaBaseAddress,
                    embedEndpoint = OLLAMA_EMBED_ENDPOINT,
                    embedInputModel = EmbedInputModel(
                        model = embeddingModel,
                        input = listOf(query)
                    )
                )
                    .onRight { response ->
                        _chatState.update { it.copy(embedResponse = response) }
                        response.embeddings.forEachIndexed { _, queryEmbedding ->
                            var jointContext = ""
                            chunkDatabase.getSimilarChunks(
                                docIds = fileIds,
                                queryEmbedding = queryEmbedding,
                            ).forEach {
                                jointContext += " " + it.second.chunkData
                            }
                            _chatState.update {
                                it.copy(
                                    retrievedContext = jointContext,
                                    isRetrievedContextReady = true
                                )
                            }
                        }
                    }
                    .onLeft { error ->
                        _chatState.update {
                            it.copy(
                                embedError = error,
                                retrievedContext = "",
                                isRetrievedContextReady = false
                            )
                        }
                    }
            }
        }
        return job
    }



    private suspend fun uploadChatToDatabase(chatModel: ChatModel){
        ollamaRepository.updateDbItem(chatModel = chatModel)
    }

    private suspend fun killOllama(){
        ollamaRepository.postOllamaChat(
            baseUrl = chatState.value.ollamaBaseAddress,
            chatEndpoint = OLLAMA_CHAT_ENDPOINT,
            chatInputModel = ChatInputModel(
                model = chatState.value.chatModel.modelName,
                messages = emptyList(),
                stream = false,
                keepAlive = 0,
            )
        )
    }

}