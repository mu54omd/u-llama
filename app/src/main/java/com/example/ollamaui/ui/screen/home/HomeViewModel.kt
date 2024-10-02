package com.example.ollamaui.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ollamaui.R
import com.example.ollamaui.activity.MainViewModel
import com.example.ollamaui.domain.model.Author
import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.EmptyTagResponse
import com.example.ollamaui.domain.model.MessageModel
import com.example.ollamaui.domain.preferences.LocalUserManager
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.utils.Constants.OLLAMA_BASE_ENDPOINT
import com.example.ollamaui.utils.Constants.OLLAMA_BASE_URL
import com.example.ollamaui.utils.Constants.OLLAMA_LIST_ENDPOINT
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
    private val ollamaRepository: OllamaRepository,
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

    fun setOllamaBaseAddress(url: String){
        _homeState.update { it.copy(ollamaBaseAddress = url) }
        refresh()
    }

    fun reloadDatabase(){
        getChats()
    }

    fun addNewChat(chatTitle: String, yourName: String, chatIcon: Int){
        val id = Random.nextInt()
        val receiverAuthor = Author(id = id, name = homeState.value.selectedModel)
        val chatModel = ChatModel(
            chatId = id,
            chatTitle = chatTitle,
            modelName = homeState.value.selectedModel,
            chatIcon = chatIcon,
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
            ollamaRepository.getOllamaStatus(baseUrl = homeState.value.ollamaBaseAddress, baseEndpoint = OLLAMA_BASE_ENDPOINT)
                .onRight { response ->  _homeState.update { it.copy(ollamaStatus = response, statusError = null, stateThrowable = null) } }
                .onLeft { error -> _homeState.update { it.copy(ollamaStatus = "", statusError = error.error.message, stateThrowable = error.t.message) } }
            ollamaRepository.getOllamaModelsList(baseUrl = homeState.value.ollamaBaseAddress, tagEndpoint = OLLAMA_LIST_ENDPOINT)
                .onRight {
                        response ->
                    response.models.forEach { model -> modelList.add(model.model) }
                    _homeState.update { it.copy(tagResponse = response, modelList = modelList, isModelListLoaded = true, tagError = null, tagThrowable = null) }
                }
                .onLeft { error -> _homeState.update { it.copy(tagResponse = EmptyTagResponse.emptyTagResponse, tagError = error.error.message, tagThrowable = error.t.message) } }
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