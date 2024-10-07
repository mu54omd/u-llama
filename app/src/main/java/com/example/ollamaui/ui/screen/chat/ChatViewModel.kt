package com.example.ollamaui.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.model.ChatInputModel
import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.EmptyChatModel
import com.example.ollamaui.domain.model.EmptyChatResponse
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.utils.Constants.OLLAMA_CHAT_ENDPOINT
import com.example.ollamaui.utils.Constants.USER_ROLE
import dagger.hilt.android.lifecycle.HiltViewModel
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

    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    fun sendButton(text: String){
        val messages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        val oldChatModel = chatState.value.chatModel
        messages.add(MessageModel(content = text, role = USER_ROLE))
        _chatState.update { it.copy(
            chatModel = ChatModel(
                chatId = oldChatModel.chatId,
                chatIcon = oldChatModel.chatIcon,
                chatTitle = oldChatModel.chatTitle,
                chatMessages = MessagesModel( messageModels = messages),
                modelName = oldChatModel.modelName,
                yourName = oldChatModel.yourName,
                botName = oldChatModel.botName
            )
        )
        }
        ollamaPostMessage(MessagesModel(messageModels = messages))
    }

    fun loadStates(chatModel: ChatModel, url: String) {
        _chatState.update {
            it.copy(
                chatModel = chatModel,
                ollamaBaseAddress = url,
                isChatScreenOpen = true,
            )
        }
    }

    fun clearStates(){
        _chatState.update { it.copy(
            chatModel = EmptyChatModel.empty,
            chatResponse = EmptyChatResponse.empty,
            chatError = null,
            isResponding = false,
            isChatScreenOpen = false,
            isChatDatabaseChanged = false
        ) }
    }

    fun uploadChatToDatabase(chatModel: ChatModel){
        viewModelScope.launch {
            ollamaRepository.updateDbItem(chatModel = chatModel)
            _chatState.update { it.copy(isChatDatabaseChanged = true) }
        }
    }

    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    private fun ollamaPostMessage(messages: MessagesModel){
        val oldMessages = chatState.value.chatModel.chatMessages.messageModels.toMutableList()
        val oldChatModel = chatState.value.chatModel
        viewModelScope.launch {
            _chatState.update { it.copy(isResponding = true) }
            ollamaRepository.postOllamaChat(
                baseUrl = chatState.value.ollamaBaseAddress,
                chatEndpoint = OLLAMA_CHAT_ENDPOINT,
                chatInputModel = ChatInputModel(
                    model = chatState.value.chatModel.modelName,
                    messages = messages.messageModels,
                    stream = false,
                )
            ).onRight { response ->
                oldMessages.add(MessageModel(content = response.message.content, role = response.message.role))
                _chatState.update { it.copy(
                    chatModel = ChatModel(
                            chatId = oldChatModel.chatId,
                            chatIcon = oldChatModel.chatIcon,
                            chatTitle = oldChatModel.chatTitle,
                            chatMessages = MessagesModel( messageModels = oldMessages ),
                            modelName = oldChatModel.modelName,
                            yourName = oldChatModel.yourName,
                        botName = oldChatModel.botName
                        ),
                    chatResponse = response,
                    isResponding = false,
                    chatError = null
                    )
                }
                uploadChatToDatabase(
                    chatModel = ChatModel(
                        chatId = oldChatModel.chatId,
                        chatIcon = oldChatModel.chatIcon,
                        chatTitle = oldChatModel.chatTitle,
                        chatMessages = MessagesModel( messageModels = oldMessages),
                        modelName = oldChatModel.modelName,
                        yourName = oldChatModel.yourName,
                        botName = oldChatModel.botName
                    ),
                )
            }.onLeft { error ->
                _chatState.update {
                    it.copy(
                        chatError = error.error.message,
                        isResponding = false,
                        chatResponse = EmptyChatResponse.empty
                    )
                }
            }
        }
    }
}