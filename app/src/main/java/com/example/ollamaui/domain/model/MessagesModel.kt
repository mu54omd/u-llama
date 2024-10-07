package com.example.ollamaui.domain.model

data class MessagesModel(
    val messageModels: List<MessageModel>,
)

object EmptyMessageModel{
    val empty = MessagesModel(
        messageModels = emptyList(),
    )
}