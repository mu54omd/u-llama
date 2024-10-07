package com.example.ollamaui.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.model.Author
import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.domain.repository.OllamaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
):ViewModel(){

    private val _chatsList = ollamaRepository.getChats().map { ChatsList(items = it) }
    val chatsList = _chatsList
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            initialValue = ChatsList()
        )

    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/

    fun addNewChat(chatTitle: String, yourName: String, chatIcon: Int, selectedModel: String){
        viewModelScope.launch {
            val id = ollamaRepository.getLastId().plus(1)
            val receiverAuthor = Author(id = id, name = selectedModel)
            val chatModel = ChatModel(
                chatTitle = chatTitle,
                modelName = selectedModel,
                chatIcon = chatIcon,
                chatMessages = MessagesModel(messageModels = emptyList(), receiver = receiverAuthor),
                context = emptyList(),
                yourName = yourName
            )
            ollamaRepository.insertToDb(chatModel)
        }
    }

    fun deleteChat(chatModel: ChatModel){
        viewModelScope.launch {
            ollamaRepository.deleteFromDb(chatModel)
        }
    }
    fun deleteChatById(chatId: Int){
        viewModelScope.launch {
            ollamaRepository.deleteFromDbById(chatId)
        }
    }
    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/

}