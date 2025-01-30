package com.example.ollamaui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ollamaui.domain.model.pull.EmptyPullResponse
import com.example.ollamaui.domain.model.pull.PullInputModel
import com.example.ollamaui.domain.model.tag.EmptyTagResponse
import com.example.ollamaui.domain.preferences.LocalUserManager
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.utils.Constants.OLLAMA_BASE_ENDPOINT
import com.example.ollamaui.utils.Constants.OLLAMA_LIST_ENDPOINT
import com.example.ollamaui.utils.Constants.OLLAMA_PULL_ENDPOINT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
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
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private val _embeddingModel = userLocalUserManager.readOllamaEmbeddingModel().map {
        EmbeddingModel(
            isEmbeddingModelSet = it.isEmbeddingModelSet,
            embeddingModelName = it.embeddingModelName
        )
    }
    val embeddingModel = _embeddingModel
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = EmbeddingModel()
        )
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private val _mainState = MutableStateFlow(MainStates())
    val mainState = _mainState
        .onStart {
            ollamaStatus()
            loadEmbeddingModelList()
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

    fun refresh(){
        ollamaStatus()
    }

    fun checkOllamaAddress(url: String){
        _mainState.update { it.copy(ollamaStatus = "", statusError = null) }
        viewModelScope.launch {
            getOllamaStatus(url = url)
            getOllamaModelsList()
        }
    }
    fun pullEmbeddingModel(modelName: String){
        _mainState.update { it.copy(pullResponse = EmptyPullResponse.empty, pullError = null) }
        val modelList = mainState.value.modelList.map { it.split(":")[0] }
        if(modelName !in modelList) {
            viewModelScope.launch {
                ollamaPostPull(modelName = modelName)
            }
        }else{
            _mainState.update { it.copy(isEmbeddingModelPulled = true) }
            saveOllamaEmbeddingModel(modelName = modelName)
        }
    }

    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    private fun saveOllamaAddress(url: String){
        viewModelScope.launch {
            userLocalUserManager.saveOllamaUrl(url = url)
        }
    }
    private fun saveOllamaEmbeddingModel(modelName: String){
        viewModelScope.launch {
            userLocalUserManager.saveOllamaEmbeddingModel(modelName = modelName)
        }
    }
    private fun ollamaStatus(){
        viewModelScope.launch {
            _mainState.update { it.copy(isModelListLoaded = false) }
            if(mainState.value.launchAppGetStatusTry == 0) {
                for (retryCount in 1..2) {
                    getOllamaStatus(baseAddress.value.ollamaBaseAddress)
                    _mainState.update { it.copy(launchAppGetStatusTry = mainState.value.launchAppGetStatusTry.plus(1)) }
                }
            }else{
                getOllamaStatus(baseAddress.value.ollamaBaseAddress)
            }
            getOllamaModelsList()
            val modelList = mainState.value.modelList.map { it.split(":")[0] }
            if(embeddingModel.value.isEmbeddingModelSet) {
                if (embeddingModel.value.embeddingModelName !in modelList) {
                    ollamaPostPull(modelName = embeddingModel.value.embeddingModelName)
                } else {
                    _mainState.update { it.copy(isEmbeddingModelPulled = true) }
                }
            }
        }
    }

    private suspend fun getOllamaStatus(url: String){
        ollamaRepository.getOllamaStatus(
            baseUrl = url,
            baseEndpoint = OLLAMA_BASE_ENDPOINT
        )
            .onRight { response ->
                _mainState.update {
                    it.copy(
                        ollamaStatus = response,
                        statusError = null,
                    )
                }
                if(baseAddress.value.ollamaBaseAddress != url) {
                    saveOllamaAddress(url = url)
                }
            }
            .onLeft { statusError ->
                _mainState.update {
                    it.copy(
                        ollamaStatus = "",
                        statusError = statusError,
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
                    )
                }
            }
            .onLeft { tagError ->
                _mainState.update {
                    it.copy(
                        tagResponse = EmptyTagResponse.emptyTagResponse,
                        tagError = tagError,
                    )
                }
            }
    }
    private suspend fun ollamaPostPull(modelName: String){

        _mainState.update { it.copy(isEmbeddingModelPulling = true, isEmbeddingModelPulled = false) }
        ollamaRepository.postOllamaPull(
            baseUrl = baseAddress.value.ollamaBaseAddress,
            pullEndpoint = OLLAMA_PULL_ENDPOINT,
            pullInputModel = PullInputModel(
                name = modelName,
                stream = false
            )
        )
            .onRight { response ->
                _mainState.update { it.copy(pullResponse = response, isEmbeddingModelPulling = false, isEmbeddingModelPulled = true) }
                if(embeddingModel.value.embeddingModelName != modelName) {
                    saveOllamaEmbeddingModel(modelName = modelName)
                }
            }
            .onLeft { pullError ->
                _mainState.update {
                    it.copy(
                        pullError = pullError,
                        isEmbeddingModelPulling = false)
                }
            }
    }
    private fun loadEmbeddingModelList(){
        viewModelScope.launch {
            try{
                val url = "https://ollama.com/search?c=embedding"
                withContext(Dispatchers.IO) {
                    val doc = Jsoup.connect(url).get()
                    val result = doc
                        .getElementsByClass("truncate text-xl font-medium underline-offset-2 group-hover:underline md:text-2xl")
                        .text()
                        .split(" ")
                    _mainState.update { it.copy(embeddingModelList = result) }
                }
            }catch (e: IOException){
                Log.d("cTAG", "exception: $e")
            }
        }
    }
}