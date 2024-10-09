package com.example.ollamaui.ui.screen.home

import com.example.ollamaui.domain.model.ChatModel

data class ChatsList(
    val items: List<ChatModel> = emptyList(),
)