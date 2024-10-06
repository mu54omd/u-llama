package com.example.ollamaui.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.model.Author
import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.MessagesModel
import com.example.ollamaui.domain.repository.OllamaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
):ViewModel(){

    private val _homeState = MutableStateFlow(HomeStates())
    val homeState = _homeState
        .onStart {

        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            initialValue = HomeStates()
        )

    private val _chatList = ollamaRepository.getChats().map { HomeListState(it) }
    val chatList = _chatList
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            initialValue = HomeListState()
        )

    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    fun selectOllamaModel(modelName: String){
        _homeState.update { it.copy(selectedModel = modelName) }
    }



    fun addNewChat(chatTitle: String, yourName: String, chatIcon: Int){
        viewModelScope.launch {
            val id = ollamaRepository.getLastId().plus(1)
            val receiverAuthor = Author(id = id, name = homeState.value.selectedModel)
            val chatModel = ChatModel(
                chatTitle = chatTitle,
                modelName = homeState.value.selectedModel,
                chatIcon = chatIcon,
                chatMessages = MessagesModel(messageModels = emptyList(), receiver = receiverAuthor),
                context = emptyList(),
                yourName = yourName
            )
            ollamaRepository.insertToDb(chatModel)
        }
    }

    fun selectChat(chatModel: ChatModel){
        _homeState.update { it.copy(selectedChat = chatModel) }
    }
    fun deselectChat(){
        _homeState.update { it.copy(selectedChat = null) }
    }

    fun deleteChat(chatModel: ChatModel){
        viewModelScope.launch {
            ollamaRepository.deleteFromDb(chatModel)
        }
    }
    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/

}