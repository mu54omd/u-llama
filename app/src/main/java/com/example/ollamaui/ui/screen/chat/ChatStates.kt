package com.example.ollamaui.ui.screen.chat

import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.domain.model.chat.ChatModel
import com.example.ollamaui.domain.model.chat.ChatResponse
import com.example.ollamaui.domain.model.chat.EmptyChatModel
import com.example.ollamaui.domain.model.chat.EmptyChatResponse
import com.example.ollamaui.domain.model.embed.EmbedResponse
import com.example.ollamaui.domain.model.embed.EmptyEmbedResponse
import com.example.ollamaui.domain.model.pull.EmptyPullResponse
import com.example.ollamaui.domain.model.pull.PullResponse

data class ChatStates(
    val ollamaBaseAddress: String = "",
    val chatModel: ChatModel = EmptyChatModel.empty,
    val chatResponse: ChatResponse = EmptyChatResponse.empty,
    val chatError: NetworkError? = null,

    val isRespondingList: List<Int> = emptyList(),
    val isSendingFailed: Boolean = false,
    val isDatabaseChanged: Boolean = false,

    val attachImageResult: String? = null,
    val attachImageError: String? = null,
    val attachDocResult: String? = null,
    val attachDocError: String? = null,

    val embedResponse: EmbedResponse = EmptyEmbedResponse.empty,
    val embedError: NetworkError? = null,
)