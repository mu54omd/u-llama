package com.example.ollamaui.domain.model

data class ChatInputModel(
    val model: String,
    val messages: List<MessageModel>,
    val stream: Boolean,
)