package com.example.ollamaui.ui.screen.chat

import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.domain.model.chat.ChatModel
import com.example.ollamaui.domain.model.chat.ChatResponse
import com.example.ollamaui.domain.model.chat.DefaultModelParameters
import com.example.ollamaui.domain.model.chat.EmptyChatModel
import com.example.ollamaui.domain.model.chat.EmptyChatResponse
import com.example.ollamaui.domain.model.chat.ModelParameters
import com.example.ollamaui.domain.model.embed.EmbedResponse
import com.example.ollamaui.domain.model.embed.EmptyEmbedResponse

data class ChatStates(
    val ollamaBaseAddress: String = "",
    val chatModel: ChatModel = EmptyChatModel.empty,
    val chatResponse: ChatResponse = EmptyChatResponse.empty,
    val chatError: NetworkError? = null,
    val chatOptions: ModelParameters = DefaultModelParameters.default,

    val isRespondingList: List<Int> = emptyList(),
    val isSendingFailed: Boolean = false,
    val isDatabaseChanged: Boolean = false,

    val embedResponse: EmbedResponse = EmptyEmbedResponse.empty,
    val embedError: NetworkError? = null,

    val retrievedContext: String = "",
    val isRetrievedContextReady: Boolean = false
)