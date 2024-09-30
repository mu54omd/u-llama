package com.example.ollamaui.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.model.Author
import com.example.ollamaui.domain.model.ChatInputModel
import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.EmptyChatModel
import com.example.ollamaui.domain.model.Message
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.repository.OllamaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository
):ViewModel() {

    private val _chatState = MutableStateFlow(ChatStates())
    val chatState = _chatState.asStateFlow()

    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    fun sendButton(text: String){
        val messages = chatState.value.chatModel.chatMessages.messages.toMutableList()
        val oldChatModel = chatState.value.chatModel
        messages.add(Message(text = text, author = Author(0, "Musa")))

        _chatState.update { it.copy(
            chatModel = ChatModel(
                chatId = oldChatModel.chatId,
                chatIcon = oldChatModel.chatIcon,
                chatTitle = oldChatModel.chatTitle,
                chatMessages = MessageModel( messages = messages, receiver = Author(id = oldChatModel.chatId, name = oldChatModel.modelName)),
                context = oldChatModel.context,
                modelName = oldChatModel.modelName
            )
        )
        }
        ollamaPostMessage(text = text)
    }

    fun loadStates(chatId: Int) {
        viewModelScope.launch {
            val newChatModel = ollamaRepository.getChat(chatId) ?: EmptyChatModel.empty
            _chatState.update {
                it.copy(
                    chatModel = ChatModel(
                        chatId = newChatModel.chatId,
                        chatIcon = newChatModel.chatIcon,
                        chatTitle = newChatModel.chatTitle,
                        chatMessages = newChatModel.chatMessages,
                        context = newChatModel.context,
                        modelName = newChatModel.modelName
                    )
                )
            }
        }
    }

    fun uploadChatToDatabase(chatModel: ChatModel){
        viewModelScope.launch {

            ollamaRepository.updateDbItem(chatModel = chatModel)
        }
    }

    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    private fun ollamaPostMessage(text:String){
        val messages = chatState.value.chatModel.chatMessages.messages.toMutableList()
        val oldChatModel = chatState.value.chatModel
        viewModelScope.launch {
            _chatState.update { it.copy(isResponding = true) }
            ollamaRepository.postOllamaChat(
                ChatInputModel(
                    model = chatState.value.chatModel.modelName,
                    prompt = text,
                    stream = false,
                    context = chatState.value.chatResponse.context
                )
            ).onRight { response ->
                messages.add(Message(text = response.response, author = Author(id = oldChatModel.chatId, name = oldChatModel.modelName)))
                _chatState.update { it.copy(
                        chatModel = ChatModel(
                            chatId = oldChatModel.chatId,
                            chatIcon = oldChatModel.chatIcon,
                            chatTitle = oldChatModel.chatTitle,
                            chatMessages = MessageModel( messages = messages, receiver = Author(id = oldChatModel.chatId, name = oldChatModel.modelName)),
                            context = response.context,
                            modelName = oldChatModel.modelName
                        )
                    )
                }
                _chatState.update { it.copy(chatResponse = response) }
                _chatState.update { it.copy(isResponding = false) }
            }.onLeft { error ->
                _chatState.update { it.copy(chatError = error.error.message) }
                _chatState.update { it.copy(isResponding = false) }
            }
        }
    }


}