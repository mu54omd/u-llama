package com.example.ollamaui.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.R
import com.example.ollamaui.domain.model.Author
import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.repository.OllamaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository
):ViewModel(){

    private val _homeState = MutableStateFlow(HomeStates())
    val homeState = _homeState
        .onStart {
            ollamaStatus()
            getChats()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            initialValue = HomeStates()
        )
    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    fun selectOllamaModel(modelName: String){
        _homeState.update { it.copy(selectedModel = modelName) }
    }

    fun refresh(){
        ollamaStatus()
    }
    fun reloadDatabase(){
        getChats()
    }

    fun addNewChat(chatTitle: String, yourName: String){
        val id = Random.nextInt()
        val receiverAuthor = Author(id = id, name = homeState.value.selectedModel)
        val chatModel = ChatModel(
            chatId = id,
            chatTitle = chatTitle,
            modelName = homeState.value.selectedModel,
            chatIcon = R.drawable.ic_launcher_foreground,
            chatMessages = MessageModel(messages = emptyList(), receiver = receiverAuthor),
            context = emptyList(),
            yourName = yourName
        )
        viewModelScope.launch {
            ollamaRepository.insertToDb(chatModel)
        }
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
    private fun ollamaStatus(){
        val modelList = mutableListOf<String>()
        viewModelScope.launch {
            _homeState.update { it.copy(isModelListLoaded = false) }
            ollamaRepository.getOllamaStatus()
                .onRight { response ->  _homeState.update { it.copy(ollamaStatus = response) } }
                .onLeft { error -> _homeState.update { it.copy(statusError = error.error.message) } }
            ollamaRepository.getOllamaModelsList()
                .onRight {
                        response ->
                    _homeState.update { it.copy(tagResponse = response) }
                    response.models.forEach { model -> modelList.add(model.model) }
                    _homeState.update { it.copy(modelList = modelList) }
                    _homeState.update { it.copy(isModelListLoaded = true) }
                }
                .onLeft { error -> _homeState.update { it.copy(tagError = error.error.message, tagThrowable = error.t.message) } }
        }
    }

    private fun getChats(){
        viewModelScope.launch {
            _homeState.update {
                it.copy(chatList = ollamaRepository.getChats().first())
            }
        }
    }
}