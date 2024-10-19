package com.example.ollamaui.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.model.ApiError
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.domain.model.chat.ChatInputModel
import com.example.ollamaui.domain.model.chat.ChatModel
import com.example.ollamaui.domain.model.chat.EmptyChatModel
import com.example.ollamaui.domain.model.chat.EmptyChatResponse
import com.example.ollamaui.domain.model.embed.EmbedInputModel
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.utils.Constants.OLLAMA_CHAT_ENDPOINT
import com.example.ollamaui.utils.Constants.OLLAMA_EMBED_ENDPOINT
import com.example.ollamaui.utils.Constants.USER_ROLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
):ViewModel() {

    private val _chatState = MutableStateFlow(ChatStates())
    val chatState = _chatState.asStateFlow()
    private val isRespondingList = mutableListOf<Int>()
    private val jobs = mutableMapOf<Int, Job>()

    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    fun sendButton(text: String){
        val messages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        val oldChatModel = chatState.value.chatModel
        if(chatState.value.attachImageResult != null){
            messages.add(MessageModel(content = text, role = USER_ROLE, images = listOf(chatState.value.attachImageResult!!)))
        }else {
            messages.add(MessageModel(content = text, role = USER_ROLE))
        }
        _chatState.update { it.copy(
            chatModel = ChatModel(
                chatId = oldChatModel.chatId,
                chatIcon = oldChatModel.chatIcon,
                chatTitle = oldChatModel.chatTitle,
                chatMessages = MessagesModel( messageModels = messages),
                modelName = oldChatModel.modelName,
                userName = oldChatModel.userName,
                botName = oldChatModel.botName,
            ),
            isDatabaseChanged = true
        )
        }
        ollamaPostMessage(messages = MessagesModel(messageModels = messages), chatId = oldChatModel.chatId)
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
        messages.removeLast()
        _chatState.update {
            it.copy(
                chatModel = chatState.value.chatModel.copy(chatMessages = MessagesModel(messageModels = messages), newMessageStatus = 0),
                isDatabaseChanged = true,
                isSendingFailed = false,
                chatError = null
            )
        }
    }

    fun loadStates(chatModel: ChatModel, url: String) {
        _chatState.update {
            it.copy(
                chatModel = chatModel,
                ollamaBaseAddress = url,
                isSendingFailed = chatModel.newMessageStatus == 2
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

    fun attachImageToChat(attachImageResult: String?, attachImageError: String?){
        _chatState.update { it.copy(attachImageResult = attachImageResult, attachImageError = attachImageError) }
    }

    fun attachDocumentToChat(attachDocResult: String, attachDocError: String?){

        //TODO("this is temporarily. perhaps it must move to database")
        _chatState.update { it.copy(attachDocResult = attachDocResult, attachDocError = attachDocError) }
        if(attachDocError == null){
            ollamaPostEmbed(text = listOf(attachDocResult))
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
            if(!isRespondingList.contains(chatState.value.chatModel.chatId)) isRespondingList.add(chatId)
            _chatState.update { it.copy(isRespondingList = isRespondingList) }
            ollamaRepository.postOllamaChat(
                baseUrl = chatState.value.ollamaBaseAddress,
                chatEndpoint = OLLAMA_CHAT_ENDPOINT,
                chatInputModel = ChatInputModel(
                    model = chatState.value.chatModel.modelName,
                    messages = messages.messageModels,
                    keepAlive = 3600,
                    stream = false,
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
            }.onLeft { error ->
                isRespondingList.remove(chatId)
                _chatState.update { it.copy(isRespondingList = isRespondingList, isSendingFailed = true) }
                if(chatState.value.chatModel.chatId == oldChatModel.chatId) {
                    _chatState.update {
                        it.copy(
                            chatError = error,
                            chatResponse = EmptyChatResponse.empty
                        )
                    }
                }
                uploadChatToDatabase(oldChatModel.copy(newMessageStatus = 2))
            }
        }
    }
    private fun ollamaPostEmbed(text: List<String>){
        viewModelScope.launch {
            ollamaRepository.postOllamaEmbed(
                baseUrl = chatState.value.ollamaBaseAddress,
                embedEndpoint = OLLAMA_EMBED_ENDPOINT,
                embedInputModel = EmbedInputModel(
                    model = "all-minilm",
                    input = text
                )
            )
                .onRight { response ->
                    //TODO("Upload to Database")
                    _chatState.update { it.copy(embedResponse = response) }
                }
                .onLeft { error ->
                    _chatState.update { it.copy(embedError = error) }
                }
        }
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
                keepAlive = 0
            )
        )
    }
}