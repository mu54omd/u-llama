package com.example.ollamaui.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.model.Author
import com.example.ollamaui.domain.model.ChatInputModel
import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.EmptyChatModel
import com.example.ollamaui.domain.model.EmptyChatResponse
import com.example.ollamaui.domain.model.Message
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.utils.Constants.OLLAMA_CHAT_ENDPOINT
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
        val messages = chatState.value.chatModel.chatMessages.messages.toMutableList()
        val oldChatModel = chatState.value.chatModel
        messages.add(Message(text = text, author = Author(0, name = oldChatModel.yourName)))

        _chatState.update { it.copy(
            chatModel = ChatModel(
                chatId = oldChatModel.chatId,
                chatIcon = oldChatModel.chatIcon,
                chatTitle = oldChatModel.chatTitle,
                chatMessages = MessageModel( messages = messages, receiver = Author(id = oldChatModel.chatId, name = oldChatModel.modelName)),
                context = oldChatModel.context,
                modelName = oldChatModel.modelName,
                yourName = oldChatModel.yourName
            )
        )
        }
        ollamaPostMessage(text = text)
    }

    fun loadStates(chatId: Int, url: String) {
        viewModelScope.launch {
            val newChatModel = ollamaRepository.getChat(chatId)!!
            _chatState.update {
                it.copy(
                    chatModel = ChatModel(
                        chatId = newChatModel.chatId,
                        chatIcon = newChatModel.chatIcon,
                        chatTitle = newChatModel.chatTitle,
                        chatMessages = newChatModel.chatMessages,
                        context = newChatModel.context,
                        modelName = newChatModel.modelName,
                        yourName = newChatModel.yourName
                    ),
                    ollamaBaseAddress = url
                )
            }
        }
    }

    fun clearStates(){
        _chatState.update { it.copy(chatModel = EmptyChatModel.empty, chatResponse = EmptyChatResponse.empty, chatError = null, isResponding = false) }
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
        val context = chatState.value.chatModel.context.toMutableList()
        val oldChatModel = chatState.value.chatModel
        viewModelScope.launch {
            _chatState.update { it.copy(isResponding = true) }
            ollamaRepository.postOllamaChat(
                baseUrl = chatState.value.ollamaBaseAddress,
                chatEndpoint = OLLAMA_CHAT_ENDPOINT,
                ChatInputModel(
                    model = chatState.value.chatModel.modelName,
                    prompt = text,
                    stream = false,
                    context = context
                )
            ).onRight { response ->
                messages.add(Message(text = response.response, author = Author(id = oldChatModel.chatId, name = oldChatModel.modelName)))
                context.addAll(response.context)
                _chatState.update { it.copy(
                    chatModel = ChatModel(
                            chatId = oldChatModel.chatId,
                            chatIcon = oldChatModel.chatIcon,
                            chatTitle = oldChatModel.chatTitle,
                            chatMessages = MessageModel( messages = messages, receiver = Author(id = oldChatModel.chatId, name = oldChatModel.modelName)),
                            context = context,
                            modelName = oldChatModel.modelName,
                            yourName = oldChatModel.yourName
                        ),
                    chatResponse = response,
                    isResponding = false
                    )
                }
            }.onLeft { error ->
                _chatState.update {
                    it.copy(
                        chatError = error.error.message,
                        isResponding = false
                    )
                }
            }
        }
    }


}