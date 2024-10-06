package com.example.ollamaui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.model.EmptyTagResponse
import com.example.ollamaui.domain.preferences.LocalUserManager
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.utils.Constants.OLLAMA_BASE_ENDPOINT
import com.example.ollamaui.utils.Constants.OLLAMA_LIST_ENDPOINT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
    private val userLocalUserManager: LocalUserManager,
):ViewModel() {

    private val _baseAddress = userLocalUserManager.readOllamaUrl().map {
        BaseAddress(
            ollamaBaseAddress = it.ollamaBaseAddress,
            isOllamaAddressSet = it.isOllamaAddressSet,
        )
    }
    val baseAddress = _baseAddress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = BaseAddress()
        )

    private val _mainState = MutableStateFlow(MainStates())
    val mainState = _mainState
        .onStart {
            ollamaStatus()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = MainStates()
        )


    //Public methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    fun saveLocalSetting(url: String){
        viewModelScope.launch {
            userLocalUserManager.saveOllamaUrl(url = url)
            refresh()
        }
    }

    fun refresh(){
        ollamaStatus()
    }

    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/

    private fun ollamaStatus(){
        val modelList = mutableListOf<String>()
        viewModelScope.launch {
            _mainState.update { it.copy(isModelListLoaded = false) }
            ollamaRepository.getOllamaStatus(baseUrl = baseAddress.value.ollamaBaseAddress, baseEndpoint = OLLAMA_BASE_ENDPOINT)
                .onRight { response ->  _mainState.update { it.copy(ollamaStatus = response, statusError = null, statusThrowable = null) } }
                .onLeft { error -> _mainState.update { it.copy(ollamaStatus = "", statusError = error.error.message, statusThrowable = error.t.message) } }
            ollamaRepository.getOllamaModelsList(baseUrl = baseAddress.value.ollamaBaseAddress, tagEndpoint = OLLAMA_LIST_ENDPOINT)
                .onRight {
                        response ->
                    response.models.forEach { model -> modelList.add(model.model) }
                    _mainState.update { it.copy(tagResponse = response, modelList = modelList, isModelListLoaded = true, tagError = null, tagThrowable = null) }
                }
                .onLeft { error -> _mainState.update { it.copy(tagResponse = EmptyTagResponse.emptyTagResponse, tagError = error.error.message, tagThrowable = error.t.message) } }
        }
    }
}