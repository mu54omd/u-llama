package com.example.ollamaui.ui.screen.home

import com.example.ollamaui.domain.model.ChatModel

data class HomeStates(
    val selectedModel: String = "None",
    val selectedChat: ChatModel? = null,
)