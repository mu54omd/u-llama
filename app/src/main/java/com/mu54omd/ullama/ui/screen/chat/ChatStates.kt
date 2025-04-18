package com.mu54omd.ullama.ui.screen.chat

import com.mu54omd.ullama.domain.model.NetworkError
import com.mu54omd.ullama.domain.model.chat.ChatModel
import com.mu54omd.ullama.domain.model.chat.ChatResponse
import com.mu54omd.ullama.domain.model.chat.DefaultModelParameters
import com.mu54omd.ullama.domain.model.chat.EmptyChatModel
import com.mu54omd.ullama.domain.model.chat.EmptyChatResponse
import com.mu54omd.ullama.domain.model.chat.ModelParameters
import com.mu54omd.ullama.domain.model.embed.EmbedResponse
import com.mu54omd.ullama.domain.model.embed.EmptyEmbedResponse

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