package com.mu54omd.ullama.ui.screen.chat

import android.app.Application
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mu54omd.ullama.data.local.objectbox.ChunkDatabase
import com.mu54omd.ullama.domain.model.ApiError
import com.mu54omd.ullama.domain.model.LogModel
import com.mu54omd.ullama.domain.model.MessageModel
import com.mu54omd.ullama.domain.model.MessagesModel
import com.mu54omd.ullama.domain.model.NetworkError
import com.mu54omd.ullama.domain.model.chat.ChatInputModel
import com.mu54omd.ullama.domain.model.chat.ChatModel
import com.mu54omd.ullama.domain.model.chat.ChatResponse
import com.mu54omd.ullama.domain.model.chat.EmptyChatModel
import com.mu54omd.ullama.domain.model.chat.EmptyChatResponse
import com.mu54omd.ullama.domain.model.chat.ModelParameters
import com.mu54omd.ullama.domain.model.embed.EmbedInputModel
import com.mu54omd.ullama.domain.model.objectbox.StableFile
import com.mu54omd.ullama.domain.repository.OllamaRepository
import com.mu54omd.ullama.helper.tts.TTSHelper
import com.mu54omd.ullama.mapper.toNetworkError
import com.mu54omd.ullama.utils.Constants.OLLAMA_CHAT_ENDPOINT
import com.mu54omd.ullama.utils.Constants.OLLAMA_EMBED_ENDPOINT
import com.mu54omd.ullama.utils.Constants.USER_ROLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@Stable
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
    private val chunkDatabase: ChunkDatabase,
    application: Application
):ViewModel() {

    val ttsHelper = TTSHelper(application)
    private val _isReading = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val isReading: StateFlow<Map<Int, Boolean>> = _isReading.asStateFlow()

    private val _chatState = MutableStateFlow(ChatStates())
    val chatState = _chatState.asStateFlow()

    private val isRespondingList = mutableListOf<Int>()
    private val jobs = mutableMapOf<Int, Job>()

    private val _temporaryReceivedMessage = mutableStateMapOf<Int, String?>()
    val temporaryReceivedMessage = _temporaryReceivedMessage

    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    fun sendButton(text: String, selectedFiles: List<StableFile>, embeddingModel: String){
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
                ollamaPostMessage(messages = MessagesModel(messageModels = messages), chatId = oldChatModel.chatId)
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
                ollamaPostMessage(messages = MessagesModel(messageModels = messages), chatId = oldChatModel.chatId)
            }
            else -> {
                val job =
                    getSimilarChunk(
                        fileIds = selectedFiles.filter { !it.isImage }.map{ it.fileId },
                        query = text.trim(),
                        embeddingModel = embeddingModel,
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
                    ollamaPostMessage(messages = MessagesModel(messageModels = messages), chatId = oldChatModel.chatId)
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
    fun stop(chatId: Int, chatError: NetworkError = NetworkError(error = ApiError.UnknownError, t = Throwable("The request has been cancelled by user"))){
        jobs[chatId]?.cancel()
        jobs.remove(chatId)
        isRespondingList.remove(chatId)
        _chatState.update { it.copy(
            isSendingFailed = true,
            isRespondingList = isRespondingList,
            isDatabaseChanged = true,
            chatError = chatError,
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
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                uploadChatToDatabase(chatModel = chatState.value.chatModel)
            }
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

    fun deleteLastMessage(index: Int) {
        if(index == chatState.value.chatModel.chatMessages.messageModels.lastIndex){
            removeLastDialogFromDatabase()
        }else{
            removeLastDialogFromDatabase()
            removeLastDialogFromDatabase()
        }
    }

    fun reproduceResponse() {
        val chatId = chatState.value.chatModel.chatId
        removeLastDialogFromDatabase()
        ollamaPostMessage(chatState.value.chatModel.chatMessages, chatId)
    }

    fun editLastMessage(index: Int):String {
        if(index == chatState.value.chatModel.chatMessages.messageModels.lastIndex) {
            val lastMessage = chatState.value.chatModel.chatMessages.messageModels[index].content
            removeLastDialogFromDatabase()
            return lastMessage
        }else{
            val lastMessage = chatState.value.chatModel.chatMessages.messageModels[index].content
            removeLastDialogFromDatabase()
            removeLastDialogFromDatabase()
            return lastMessage
        }
    }

    fun read(chatId: Int, text: String){
        clearReading()
        setReading(chatId, true)
        ttsHelper.speak(text){
            setReading(chatId, false)
        }
    }

    fun stopTTS(chatId: Int){
        ttsHelper.stop()
        setReading(chatId, false)
    }

    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/

    private fun setReading(chatId: Int, isReading: Boolean){
        _isReading.update { currentMap ->
            currentMap.toMutableMap().apply {
                this[chatId] = isReading
            }
        }
    }
    private fun clearReading(){
        _isReading.update { emptyMap() }
    }

    private fun ollamaPostMessage(messages: MessagesModel, chatId: Int){
        val oldMessages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        val oldChatModel = chatState.value.chatModel
        val job = jobs[chatId] ?: Job()
        jobs[chatId] = job
        viewModelScope.launch(job) {
            withContext(Dispatchers.IO) {
                if (!isRespondingList.contains(chatState.value.chatModel.chatId)) isRespondingList.add(chatId)
                _chatState.update { it.copy(isRespondingList = isRespondingList) }
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "START",
                        content = "ollama post message: ${chatState.value.ollamaBaseAddress}${OLLAMA_CHAT_ENDPOINT}",
                    )
                )
                var result = ""
                var finalResponse = EmptyChatResponse.empty
                ollamaRepository.postOllamaChat(
                    baseUrl = chatState.value.ollamaBaseAddress,
                    chatEndpoint = OLLAMA_CHAT_ENDPOINT,
                    chatInputModel = ChatInputModel(
                        model = chatState.value.chatModel.modelName,
                        messages = messages.messageModels,
                        keepAlive = 3600,
                        stream = true,
                        options = chatState.value.chatOptions
                    )
                )
                    .onEach { response ->
                        response.fold(
                            { error ->
                                stop(chatId = chatId, chatError = error)
                                uploadChatToDatabase(oldChatModel.copy(newMessageStatus = 2))
                                ollamaRepository.insertLogToDb(
                                    LogModel(
                                        date = LocalDateTime.now().toString(),
                                        type = "ERROR",
                                        content = "ollama post message: ${error.t.message}",
                                    )
                                )
                            },
                            { chatResponse ->
                                result += chatResponse.message.content
                                _temporaryReceivedMessage[chatId] = result
                                if (chatResponse.doneReason != null) {
                                    finalResponse = ChatResponse(
                                        createdAt = chatResponse.createdAt,
                                        done = chatResponse.done,
                                        doneReason = chatResponse.doneReason,
                                        model = chatResponse.model,
                                        message = MessageModel(
                                            content = result,
                                            role = chatResponse.message.role,
                                        ),
                                        totalDuration = chatResponse.totalDuration
                                    )
                                }
                            }
                        )
                    }
                    .onCompletion { cause ->
                        if (cause == null) {
                            oldMessages.add(
                                MessageModel(
                                    content = finalResponse.message.content,
                                    role = finalResponse.message.role
                                )
                            )
                            isRespondingList.remove(chatId)
                            _chatState.update { it.copy(isRespondingList = isRespondingList) }
                            _temporaryReceivedMessage[chatId] = null
                            if (chatState.value.chatModel.chatId == oldChatModel.chatId) {
                                _chatState.update {
                                    it.copy(
                                        chatModel = ChatModel(
                                            chatId = oldChatModel.chatId,
                                            chatTitle = oldChatModel.chatTitle,
                                            chatMessages = MessagesModel(messageModels = oldMessages),
                                            modelName = oldChatModel.modelName,
                                        ),
                                        chatResponse = finalResponse,
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
                                    type = "SUCCESS",
                                    content = "ollama post message: ${chatState.value.ollamaBaseAddress}${OLLAMA_CHAT_ENDPOINT}",
                                )
                            )
                        } else {
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
                                        chatError = cause.toNetworkError(),
                                        chatResponse = EmptyChatResponse.empty,
                                        chatModel = chatState.value.chatModel.copy(newMessageStatus = 2)
                                    )
                                }
                            }
                            uploadChatToDatabase(oldChatModel.copy(newMessageStatus = 2))
                            ollamaRepository.insertLogToDb(
                                LogModel(
                                    date = LocalDateTime.now().toString(),
                                    type = "ERROR",
                                    content = "ollama post message: ${cause.message}",
                                )
                            )
                        }
                    }
                    .catch { error ->
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
                                    chatError = error.toNetworkError(),
                                    chatResponse = EmptyChatResponse.empty,
                                    chatModel = chatState.value.chatModel.copy(newMessageStatus = 2)
                                )
                            }
                        }
                        uploadChatToDatabase(oldChatModel.copy(newMessageStatus = 2))
                        ollamaRepository.insertLogToDb(
                            LogModel(
                                date = LocalDateTime.now().toString(),
                                type = "ERROR",
                                content = "ollama post message: ${error.message}",
                            )
                        )
                    }
                    .collect()
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

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}