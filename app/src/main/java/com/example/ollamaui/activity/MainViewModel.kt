package com.example.ollamaui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.recover
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
            isLocalSettingsLoaded = true
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
        viewModelScope.launch {
            _mainState.update { it.copy(isModelListLoaded = false) }
            if(mainState.value.launchAppGetStatusTry == 0) {
                for (retryCount in 1..2) {
                    getOllamaStatus()
                    _mainState.update { it.copy(launchAppGetStatusTry = mainState.value.launchAppGetStatusTry.plus(1)) }
                }
            }else{
                getOllamaStatus()
            }
            getOllamaModelsList()
        }
    }

    private suspend fun getOllamaStatus(){
        ollamaRepository.getOllamaStatus(
            baseUrl = baseAddress.value.ollamaBaseAddress,
            baseEndpoint = OLLAMA_BASE_ENDPOINT
        )
            .onRight { response ->
                _mainState.update {
                    it.copy(
                        ollamaStatus = response,
                        statusError = null,
                        statusThrowable = null
                    )
                }
            }
            .onLeft { error ->
                _mainState.update {
                    it.copy(
                        ollamaStatus = "",
                        statusError = error.error.message,
                        statusThrowable = error.t.message
                    )
                }
            }
    }
    private suspend fun getOllamaModelsList(){
        val modelList = mutableListOf<String>()
        ollamaRepository.getOllamaModelsList(baseUrl = baseAddress.value.ollamaBaseAddress, tagEndpoint = OLLAMA_LIST_ENDPOINT)
            .onRight { response ->
                response.models.forEach { model -> modelList.add(model.model) }
                _mainState.update {
                    it.copy(
                        tagResponse = response,
                        modelList = modelList,
                        isModelListLoaded = true,
                        tagError = null,
                        tagThrowable = null
                    )
                }
            }
            .onLeft { error ->
                _mainState.update {
                    it.copy(
                        tagResponse = EmptyTagResponse.emptyTagResponse,
                        tagError = error.error.message,
                        tagThrowable = error.t.message
                    )
                }
            }
    }
}