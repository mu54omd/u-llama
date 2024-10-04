package com.example.ollamaui.ui.screen.home

import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.EmptyTagResponse
import com.example.ollamaui.domain.model.TagResponse

data class HomeStates(
    val ollamaBaseAddress: String = "",
    val ollamaStatus: String = "",
    val statusError: Int? = null,
    val stateThrowable: String? = null,

    val tagResponse: TagResponse = EmptyTagResponse.emptyTagResponse,
    val modelList: List<String> = emptyList(),
    val tagError: Int? = null,
    val tagThrowable: String? = null,

    val isModelListLoaded: Boolean = false,
    val selectedModel: String = "None",

    val selectedChat: ChatModel? = null,
)