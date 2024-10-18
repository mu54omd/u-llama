package com.example.ollamaui.activity

import com.example.ollamaui.domain.model.pull.EmptyPullResponse
import com.example.ollamaui.domain.model.pull.PullResponse
import com.example.ollamaui.domain.model.tag.EmptyTagResponse
import com.example.ollamaui.domain.model.tag.TagResponse

data class MainStates(
    val launchAppGetStatusTry: Int = 0,
    val isModelListLoaded: Boolean = false,
    val ollamaStatus: String = "",
    val statusError: Int? = null,
    val statusThrowable: String? = null,
    val tagError: Int? = null,
    val tagThrowable: String? = null,
    val tagResponse: TagResponse = EmptyTagResponse.emptyTagResponse,
    val modelList: List<String> = emptyList(),

    val pullResponse: PullResponse = EmptyPullResponse.empty,
    val isEmbeddingModelPulling: Boolean = false,
    val isEmbeddingModelPulled: Boolean = false,
    val pullError: Int? = null,
    val embeddingModelName: String = ""
)
