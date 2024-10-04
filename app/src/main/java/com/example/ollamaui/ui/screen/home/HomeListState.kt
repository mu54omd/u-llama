package com.example.ollamaui.ui.screen.home

import com.example.ollamaui.domain.model.ChatModel

data class HomeListState(
    val chatList: List<ChatModel> = emptyList()
)