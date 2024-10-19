package com.example.ollamaui.activity

import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.domain.model.pull.EmptyPullResponse
import com.example.ollamaui.domain.model.pull.PullResponse
import com.example.ollamaui.domain.model.tag.EmptyTagResponse
import com.example.ollamaui.domain.model.tag.TagResponse

data class MainStates(
    val launchAppGetStatusTry: Int = 0,
    val isModelListLoaded: Boolean = false,
    val ollamaStatus: String = "",
    val statusError: NetworkError? = null,
    val tagError: NetworkError? = null,
    val tagResponse: TagResponse = EmptyTagResponse.emptyTagResponse,
    val modelList: List<String> = emptyList(),

    val pullResponse: PullResponse = EmptyPullResponse.empty,
    val isEmbeddingModelPulling: Boolean = false,
    val isEmbeddingModelPulled: Boolean = false,
    val pullError: NetworkError? = null,
)
