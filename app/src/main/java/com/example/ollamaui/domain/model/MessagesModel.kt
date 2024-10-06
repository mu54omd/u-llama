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
    var test = MessagesModel(
        messageModels = listOf(
            MessageModel(
                text = "Hi",
                author = Author(id = 0, name = "Musa")
            ),
            MessageModel(
                text = "Hello!",
                author = Author(id = 1, name = "Musashi")
            ),
            MessageModel(
                text = "Where are you?",
                author = Author(id = 0, name = "Musa")
            ),
            MessageModel(
                text = "I'm home!",
                author = Author(id = 1, name = "Musashi")
            ),
            MessageModel(
                text = "Come here!",
                author = Author(id = 1, name = "Musashi")
            ),
        ),
        receiver = Author(id = 1, name = "Musashi")
    )
    val empty = MessagesModel(
        messageModels = emptyList(),
        receiver = Author(id = 1, name = "Bot")
    )
}