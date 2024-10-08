package com.example.ollamaui.ui.screen.chat

import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.ChatResponse
import com.example.ollamaui.domain.model.EmptyChatModel
import com.example.ollamaui.domain.model.EmptyChatResponse

data class ChatStates(
    val ollamaBaseAddress: String = "",
    val chatModel: ChatModel = EmptyChatModel.empty,
    val chatResponse: ChatResponse = EmptyChatResponse.empty,
    val isRespondingList: List<Int> = emptyList(),
    val chatError: Int? = null,
    val isChatScreenOpen: Boolean = false,
    val isChatDatabaseChanged: Boolean = false,
)