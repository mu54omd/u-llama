package com.example.ollamaui.activity

import com.example.ollamaui.domain.model.EmptyTagResponse
import com.example.ollamaui.domain.model.TagResponse

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
)
