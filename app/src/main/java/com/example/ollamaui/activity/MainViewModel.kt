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
            refresh()
            fetchEmbeddingModelList()
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
        viewModelScope.launch {
            _mainState.update { it.copy(isModelListLoaded = false) }
            getOllamaStatus(baseAddress.value.ollamaBaseAddress)
            getOllamaModelsList()
//            val modelList = mainState.value.fullModelList.map { it.split(":")[0] }
//            if(embeddingModel.value.isEmbeddingModelSet) {
//                if (embeddingModel.value.embeddingModelName !in modelList) {
//                    ollamaPostPull(modelName = embeddingModel.value.embeddingModelName)
//                } else {
//                    _mainState.update { it.copy(isEmbeddingModelPulled = true) }
//                }
//            }
        }
    }

    fun checkOllamaAddress(url: String){
        _mainState.update { it.copy(ollamaStatus = "", statusError = null) }
        viewModelScope.launch {
            getOllamaStatus(url = url)
        }
    }

    fun pullEmbeddingModel(modelName: String){
        _mainState.update { it.copy(pullResponse = EmptyPullResponse.empty, pullError = null) }
        if(checkIfEmbeddingModelPulled(modelName)) {
            viewModelScope.launch {
                ollamaPostPull(modelName = modelName)
            }
        }else{
            _mainState.update { it.copy(isEmbeddingModelPulled = true) }
            saveOllamaEmbeddingModel(modelName = modelName)
        }
    }

    fun checkIfEmbeddingModelPulled(modelName: String):Boolean{
        val modelList = mainState.value.fullModelList.map { it.split(":")[0] }
        _mainState.update { it.copy(isEmbeddingModelPulled = modelName in modelList) }
        return modelName in modelList
    }

    fun saveOllamaAddress(url: String){
        viewModelScope.launch {
            userLocalUserManager.saveOllamaUrl(url = url)
        }
    }
    fun saveOllamaEmbeddingModel(modelName: String){
        viewModelScope.launch {
            userLocalUserManager.saveOllamaEmbeddingModel(modelName = modelName)
        }
    }

    fun fetchEmbeddingModelList(){
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
    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/

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
            }
            .onLeft { statusError ->
                _mainState.update {
                    it.copy(
                        ollamaStatus = "Ollama is not running!",
                        statusError = statusError,
                    )
                }
            }
    }

    private suspend fun getOllamaModelsList(){
        val fullModelList = mutableListOf<String>()
        val filteredModelList = mutableListOf<String>()
        ollamaRepository.getOllamaModelsList(baseUrl = baseAddress.value.ollamaBaseAddress, tagEndpoint = OLLAMA_LIST_ENDPOINT)
            .onRight { response ->
                response.models.forEach { model ->
                    fullModelList.add(model.name)
                    if(model.model.split(":")[0] !in mainState.value.embeddingModelList)
                        filteredModelList.add(model.model)
                }
                _mainState.update {
                    it.copy(
                        tagResponse = response,
                        fullModelList = fullModelList,
                        filteredModelList = filteredModelList,
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
                        isModelListLoaded = false
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
}