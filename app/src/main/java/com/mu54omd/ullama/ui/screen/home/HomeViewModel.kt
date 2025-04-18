package com.mu54omd.ullama.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mu54omd.ullama.domain.model.MessageModel
import com.mu54omd.ullama.domain.model.MessagesModel
import com.mu54omd.ullama.domain.model.chat.ChatModel
import com.mu54omd.ullama.domain.repository.OllamaRepository
import com.mu54omd.ullama.utils.Constants.SYSTEM_ROLE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
):ViewModel(){

    private val _chatsList = ollamaRepository.getChats()
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


    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
}