package com.mu54omd.ullama.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mu54omd.ullama.domain.model.LogModel
import com.mu54omd.ullama.domain.model.chat.ModelParameters
import com.mu54omd.ullama.domain.model.pull.EmptyPullResponse
import com.mu54omd.ullama.domain.model.pull.PullInputModel
import com.mu54omd.ullama.domain.model.tag.EmptyTagResponse
import com.mu54omd.ullama.domain.preferences.LocalUserManager
import com.mu54omd.ullama.domain.repository.OllamaRepository
import com.mu54omd.ullama.helper.network.NetworkObserver
import com.mu54omd.ullama.helper.network.NetworkStatus
import com.mu54omd.ullama.utils.Constants.OLLAMA_BASE_ENDPOINT
import com.mu54omd.ullama.utils.Constants.OLLAMA_FETCH_EMBEDDING_URL
import com.mu54omd.ullama.utils.Constants.OLLAMA_LIST_ENDPOINT
import com.mu54omd.ullama.utils.Constants.OLLAMA_PULL_ENDPOINT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ollamaRepository: OllamaRepository,
    private val userLocalUserManager: LocalUserManager,
):ViewModel() {

    private val networkObserver = NetworkObserver(ollamaRepository)
    private val _networkStatus = MutableStateFlow(NetworkStatus.UNKNOWN)
    val networkStatus: StateFlow<NetworkStatus> = _networkStatus
    private var observationJob: Job? = null
    ////////////////////////////////////////////////////////////////////////////////////////////////////
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
    private val _tuningParameters = userLocalUserManager.readTuningParameters().map {
        ModelParameters(
            topK = it.topK,
            topP = it.topP,
            minP = it.minP,
            temperature = it.temperature,
            presencePenalty = it.presencePenalty,
            frequencyPenalty = it.frequencyPenalty,
            numCtx = it.numCtx
        )
    }
    val tuningParameters = _tuningParameters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ModelParameters()
        )
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private val _mainState = MutableStateFlow(MainStates())
    val mainState = _mainState
        .onStart {
            refresh()
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

    private fun startNetworkObserving(url: String){
        observationJob?.cancel()
        observationJob = viewModelScope.launch {
            networkObserver.observeConnectivity(url).collect { isConnected ->
                _networkStatus.value = isConnected
            }
        }
    }
    fun refresh(){
        viewModelScope.launch {
            _mainState.update { it.copy(isModelListLoaded = false) }
            withContext(Dispatchers.IO) {
                fetchEmbeddingModelList()
                getOllamaStatus(baseAddress.value.ollamaBaseAddress)
                getOllamaModelsList()
                startNetworkObserving(url = baseAddress.value.ollamaBaseAddress)
            }
        }
    }

    fun checkOllamaAddress(url: String){
        _mainState.update { it.copy(ollamaStatus = "", statusError = null) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getOllamaStatus(url = url)
            }
        }
    }

    fun pullEmbeddingModel(modelName: String){
        _mainState.update { it.copy(pullResponse = EmptyPullResponse.empty, pullError = null) }
        if(checkIfEmbeddingModelPulled(modelName)) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    ollamaPostPull(modelName = modelName)
                }
            }
        }else{
            _mainState.update { it.copy(isEmbeddingModelPulled = true) }
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
    fun saveOllamaTuningParameters(modelParameters: ModelParameters){
        viewModelScope.launch {
            userLocalUserManager.saveTuningParameters(modelParameters = modelParameters)
        }
    }

    fun fetchEmbeddingModelList(){
        viewModelScope.launch {
            try{
                withContext(Dispatchers.IO) {
                    val url = OLLAMA_FETCH_EMBEDDING_URL
                    ollamaRepository.insertLogToDb(
                        LogModel(
                            date = LocalDateTime.now().toString(),
                            type = "START",
                            content = "fetch embedding model: $url",
                        )
                    )
                    val doc = Jsoup.connect(url).get()
                    val result = doc
                        .getElementsByClass("truncate text-xl font-medium underline-offset-2 group-hover:underline md:text-2xl")
                        .text()
                        .split(" ")
                    val finalList = listOf<String>("Select a Model") + result
                    _mainState.update { it.copy(embeddingModelList = finalList, fetchEmbeddingModelError = null) }
                    getOllamaModelsList()
                    ollamaRepository.insertLogToDb(
                        LogModel(
                            date = LocalDateTime.now().toString(),
                            type = "SUCCESS",
                            content = "fetch embedding model: $url",
                        )
                    )
                }
            }catch (e: IOException){
                _mainState.update { it.copy(fetchEmbeddingModelError = e.message) }
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "ERROR",
                        content = "fetch embedding model: ${e.message}",
                    )
                )
            }
        }
    }
    //Private methods
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------*/

    private suspend fun getOllamaStatus(url: String){
        ollamaRepository.insertLogToDb(
            LogModel(
                date = LocalDateTime.now().toString(),
                type = "START",
                content = "ollama get status: ${url}${OLLAMA_BASE_ENDPOINT}",
            )
        )
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
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "SUCCESS",
                        content = "ollama get status: ${url}${OLLAMA_BASE_ENDPOINT}",
                    )
                )
            }
            .onLeft { statusError ->
                _mainState.update {
                    it.copy(
                        ollamaStatus = "Ollama is not running!",
                        statusError = statusError,
                    )
                }
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "ERROR",
                        content = "ollama get status: ${statusError.t.message}",
                    )
                )
            }
    }

    private suspend fun getOllamaModelsList(){
        val fullModelList = mutableListOf<String>()
        val filteredModelList = mutableListOf<String>()
        ollamaRepository.insertLogToDb(
            LogModel(
                date = LocalDateTime.now().toString(),
                type = "START",
                content = "ollama get tag: ${baseAddress.value.ollamaBaseAddress}${OLLAMA_LIST_ENDPOINT}",
            )
        )
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
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "SUCCESS",
                        content = "ollama get tag: ${baseAddress.value.ollamaBaseAddress}${OLLAMA_LIST_ENDPOINT}",
                    )
                )
            }
            .onLeft { tagError ->
                _mainState.update {
                    it.copy(
                        tagResponse = EmptyTagResponse.emptyTagResponse,
                        tagError = tagError,
                        isModelListLoaded = false
                    )
                }
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "ERROR",
                        content = "ollama get tag: ${tagError.t.message}",
                    )
                )
            }
    }
    private suspend fun ollamaPostPull(modelName: String){

        _mainState.update { it.copy(isEmbeddingModelPulling = true, isEmbeddingModelPulled = false) }
        ollamaRepository.insertLogToDb(
            LogModel(
                date = LocalDateTime.now().toString(),
                type = "START",
                content = "ollama post pull: ${baseAddress.value.ollamaBaseAddress}${OLLAMA_PULL_ENDPOINT}",
            )
        )
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
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "SUCCESS",
                        content = "ollama post pull: ${baseAddress.value.ollamaBaseAddress}${OLLAMA_PULL_ENDPOINT}",
                    )
                )
            }
            .onLeft { pullError ->
                _mainState.update {
                    it.copy(
                        pullError = pullError,
                        isEmbeddingModelPulling = false)
                }
                ollamaRepository.insertLogToDb(
                    LogModel(
                        date = LocalDateTime.now().toString(),
                        type = "ERROR",
                        content = "ollama post pull: ${pullError.t.message}",
                    )
                )
            }
    }
}