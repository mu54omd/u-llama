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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    private val _attachedDocs = fileDatabase.getAllFiles().map { AttachedFilesList(item = it.filter { file -> !file.isImage }) }
    val attachedDocs = _attachedDocs
        .stateIn(
            scope = viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = AttachedFilesList()
        )

    private val _attachedImages = fileDatabase.getAllFiles().map { AttachedFilesList(item = it.filter { file -> file.isImage }) }
    val attachedImages = _attachedImages
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
    fun sendButton(text: String, selectedImages: List<File>, selectedDocs: List<File>, embeddingModel: String){
        val messages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        val oldChatModel = chatState.value.chatModel
        val imageList = selectedImages.map { it.attachResult }
        when{
            selectedImages.isEmpty() && selectedDocs.isEmpty() -> {
                messages.add(MessageModel(content = text.trim(), role = USER_ROLE))
                _chatState.update {
                    it.copy(
                        chatModel = ChatModel(
                            chatId = oldChatModel.chatId,
                            chatIcon = oldChatModel.chatIcon,
                            chatTitle = oldChatModel.chatTitle,
                            chatMessages = MessagesModel(messageModels = messages),
                            modelName = oldChatModel.modelName,
                            userName = oldChatModel.userName,
                            botName = oldChatModel.botName,
                        ),
                        isDatabaseChanged = true
                    )
                }
                ollamaPostMessage(
                    messages = MessagesModel(messageModels = messages),
                    chatId = oldChatModel.chatId
                )
            }
            selectedImages.isNotEmpty() && selectedDocs.isEmpty() -> {
                messages.add(MessageModel(content = text.trim(), role = USER_ROLE, images = imageList))
                _chatState.update {
                    it.copy(
                        chatModel = ChatModel(
                            chatId = oldChatModel.chatId,
                            chatIcon = oldChatModel.chatIcon,
                            chatTitle = oldChatModel.chatTitle,
                            chatMessages = MessagesModel(messageModels = messages),
                            modelName = oldChatModel.modelName,
                            userName = oldChatModel.userName,
                            botName = oldChatModel.botName,
                        ),
                        isDatabaseChanged = true
                    )
                }
                ollamaPostMessage(
                    messages = MessagesModel(messageModels = messages),
                    chatId = oldChatModel.chatId
                )
            }
            selectedImages.isEmpty() -> {
                val job =
                    getSimilarChunk(
                        fileIds = selectedDocs.map{ it.fileId },
                        query = text.trim(),
                        n = 5,
                        embeddingModel = if (embeddingModel != "") embeddingModel else chatState.value.chatModel.modelName,
                    )
                job.invokeOnCompletion {
                    messages.add(
                        MessageModel(
                            content = "Using this data: {${chatState.value.retrievedContext}}. Respond to this prompt: {${text.trim()}}.",
                            role = USER_ROLE
                        )
                    )
                    _chatState.update {
                        it.copy(
                            chatModel = ChatModel(
                                chatId = oldChatModel.chatId,
                                chatIcon = oldChatModel.chatIcon,
                                chatTitle = oldChatModel.chatTitle,
                                chatMessages = MessagesModel(messageModels = messages),
                                modelName = oldChatModel.modelName,
                                userName = oldChatModel.userName,
                                botName = oldChatModel.botName,
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
            killOllama()
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
                uploadChatToDatabase(chatModel = chatModel.copy(newMessageStatus = 0))
            }
        }
    }

    fun clearStates(){
        viewModelScope.launch {
            if(chatState.value.isDatabaseChanged && chatState.value.chatModel.newMessageStatus != 2) {
                uploadChatToDatabase(chatModel = chatState.value.chatModel.copy(newMessageStatus = 0))
            }else if(chatState.value.isDatabaseChanged && chatState.value.chatModel.newMessageStatus == 2){
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

    fun attachFileToChat(
        attachResult: String?,
        attachError: String?,
        embeddingModel: String,
        fileName: String,
        documentType: String,
        isEmbeddingModelSet: Boolean
    ){
        if(documentType in listOf("png", "jpg", "jpeg")) {
            attachImageToChat(
                attachImageResult = attachResult,
                attachImageError = attachError,
                fileName = fileName,
                documentType = documentType
            )
        }else{
            if(isEmbeddingModelSet) {
                attachDocumentToChat(
                    attachDocResult = attachResult,
                    attachDocError = attachError,
                    embeddingModel = embeddingModel,
                    fileName = fileName,
                    documentType = documentType
                )
            }else{
                attachDocumentToChat(
                    attachDocResult = attachResult,
                    attachDocError = attachError,
                    embeddingModel = chatState.value.chatModel.modelName,
                    fileName = fileName,
                    documentType = documentType
                )
            }
        }
    }

    fun removeAttachedFile(index: Int, isImage: Boolean){
        if(!isImage) {
            attachedDocs.value.item[index].fileId.let {
                fileDatabase.removeFile(fileId = it)
                chunkDatabase.removeChunk(docId = it)

            }
        }else{
            attachedImages.value.item[index].fileId.let {
                fileDatabase.removeFile(fileId = it)
            }
        }
    }
    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/

    private fun attachImageToChat(attachImageResult: String?, attachImageError: String?, fileName: String, documentType: String){
        attachImageResult?.let { image ->
            val filteredImages = attachedImages.value.item.filter {
                (it.attachResult == attachImageResult)
                        && (it.fileName == fileName)
                        && (it.fileType == documentType)
                        && (it.chatId == chatState.value.chatModel.chatId)
            }
            if(filteredImages.isEmpty()) {
                fileDatabase.addFile(
                    file = File(
                        attachResult = image,
                        fileName = fileName,
                        fileType = documentType,
                        fileAddedTime = System.currentTimeMillis(),
                        chatId = chatState.value.chatModel.chatId,
                        isImage = true
                    )
                )
            }
        }
    }

    private fun attachDocumentToChat(attachDocResult: String?, attachDocError: String?, embeddingModel: String, fileName: String, documentType: String){

        attachDocResult?.let { text ->
            val filteredList = attachedDocs.value.item.filter {
                (it.attachResult == attachDocResult)
                        && (it.fileName == fileName)
                        && (it.fileType == documentType)
                        && (it.chatId == chatState.value.chatModel.chatId)
            }
            if(filteredList.isEmpty()) {
                val docId = fileDatabase.addFile(
                    file = File(
                        attachResult = text,
                        chatId = chatState.value.chatModel.chatId,
                        fileName = fileName,
                        fileType = documentType,
                        fileAddedTime = System.currentTimeMillis()
                    )
                )
                val chunks =
                    Splitter.createChunks(docText = text, chunkSize = 100, chunkOverlap = 5)
                ollamaPostEmbed(
                    text = chunks,
                    embeddingModel = embeddingModel,
                    docId = docId,
                    fileName = fileName
                )
            }

        }
    }


    private fun ollamaPostMessage(messages: MessagesModel, chatId: Int){
        val oldMessages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        val oldChatModel = chatState.value.chatModel
        val job = jobs[chatId] ?: Job()
        jobs[chatId] = job
        viewModelScope.launch(job) {
            if(!isRespondingList.contains(chatState.value.chatModel.chatId)) isRespondingList.add(chatId)
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
                oldMessages.add(MessageModel(content = response.message.content, role = response.message.role))
                isRespondingList.remove(chatId)
                _chatState.update { it.copy(isRespondingList = isRespondingList) }
                if(chatState.value.chatModel.chatId == oldChatModel.chatId) {
                    _chatState.update {
                        it.copy(
                            chatModel = ChatModel(
                                chatId = oldChatModel.chatId,
                                chatIcon = oldChatModel.chatIcon,
                                chatTitle = oldChatModel.chatTitle,
                                chatMessages = MessagesModel(messageModels = oldMessages),
                                modelName = oldChatModel.modelName,
                                userName = oldChatModel.userName,
                                botName = oldChatModel.botName
                            ),
                            chatResponse = response,
                            chatError = null
                        )
                    }
                }
                uploadChatToDatabase(
                    chatModel = ChatModel(
                        chatId = oldChatModel.chatId,
                        chatIcon = oldChatModel.chatIcon,
                        chatTitle = oldChatModel.chatTitle,
                        chatMessages = MessagesModel( messageModels = oldMessages),
                        modelName = oldChatModel.modelName,
                        userName = oldChatModel.userName,
                        botName = oldChatModel.botName,
                        newMessageStatus = if(chatState.value.chatModel.chatId == oldChatModel.chatId) 0 else 1
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
                _chatState.update { it.copy(
                    isRespondingList = isRespondingList,
                    isSendingFailed = true,
                    isDatabaseChanged = true,
                ) }
                if(chatState.value.chatModel.chatId == oldChatModel.chatId) {
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
    private fun ollamaPostEmbed(text: List<String>, embeddingModel: String, docId: Long, fileName: String){
        viewModelScope.launch {
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

    private fun getSimilarChunk(fileIds: List<Long>, query: String, n: Int, embeddingModel: String):Job{
        val job = viewModelScope.launch {
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
                        chunkDatabase.getSimilarChunks(docIds = fileIds, queryEmbedding = queryEmbedding, n = n).forEach {
                            jointContext += " " + it.second.chunkData
                        }
                        _chatState.update { it.copy(retrievedContext = jointContext, isRetrievedContextReady = true) }
                    }
                }
                .onLeft { error ->
                    _chatState.update { it.copy(embedError = error, retrievedContext = "", isRetrievedContextReady = false) }
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