package com.example.ollamaui.domain.model

data class MessagesModel(
    val messageModels: List<MessageModel>,
    val receiver: Author
)

data class MessageModel(
    val text: String,
    val author: Author
)

data class Author(
    val id: Int = -1,
    val name: String,
)

object TestMessageModel{
    val empty = MessagesModel(
        messageModels = emptyList(),
        receiver = Author(id = 1, name = "Bot")
    )
}