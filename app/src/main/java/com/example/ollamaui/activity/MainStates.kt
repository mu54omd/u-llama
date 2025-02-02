package com.example.ollamaui.activity

import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.domain.model.pull.EmptyPullResponse
import com.example.ollamaui.domain.model.pull.PullResponse
import com.example.ollamaui.domain.model.tag.EmptyTagResponse
import com.example.ollamaui.domain.model.tag.TagResponse

data class MainStates(

    val isModelListLoaded: Boolean = false,

    val fullModelList: List<String> = emptyList(),
    val filteredModelList: List<String> = emptyList(),
    val embeddingModelList: List<String> = emptyList(),

    val ollamaStatus: String = "",
    val statusError: NetworkError? = null,
    val tagError: NetworkError? = null,
    val tagResponse: TagResponse = EmptyTagResponse.emptyTagResponse,


    val pullResponse: PullResponse = EmptyPullResponse.empty,
    val pullError: NetworkError? = null,

    val isEmbeddingModelPulling: Boolean = false,
    val isEmbeddingModelPulled: Boolean = false,

)
