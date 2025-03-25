package com.example.ollamaui.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.domain.model.chat.ChatModel
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.utils.Constants.SYSTEM_ROLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
):ViewModel(){

    private val _chatsList = ollamaRepository.getChats().flowOn(Dispatchers.IO)
    val chatsList = _chatsList
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/

    fun addNewChat(
        chatTitle: String,
        systemPrompt: String,
        selectedModel: String
    ){
        val startMessage = MessageModel(
            content = systemPrompt,
            role = SYSTEM_ROLE,

        )
        viewModelScope.launch {
            val chatModel = ChatModel(
                chatTitle = chatTitle,
                modelName = selectedModel,
                chatMessages = MessagesModel(messageModels = listOf(startMessage)),
            )
            withContext(Dispatchers.IO) {
                ollamaRepository.insertToDb(chatModel)
            }

        }
    }

    fun deleteChat(chatModel: ChatModel){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                ollamaRepository.deleteFromDb(chatModel)
            }
        }
    }
    fun deleteChatById(chatId: Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                ollamaRepository.deleteFromDbById(chatId)
            }
        }
    }
    fun findChat(chatId: Int): ChatModel?{
        return chatsList.value.find { item -> item.chatId == chatId }
    }


    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
}