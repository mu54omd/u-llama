package com.example.ollamaui.domain.model

data class ChatInputModel(
    val model: String,
    val prompt: String,
    val stream: Boolean,
    val context: List<Int> = emptyList()
)