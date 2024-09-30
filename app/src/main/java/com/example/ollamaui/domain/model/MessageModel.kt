package com.example.ollamaui.domain.model

import com.example.ollamaui.domain.model.Author.Companion.MY_ID

data class MessageModel(
    val messages: List<Message>,
    val receiver: Author
)

data class Message(
    val text: String,
    val author: Author
){
    val isFromMe: Boolean
        get() = author.id == MY_ID
}

data class Author(
    val id: Int,
    val name: String,
){
    companion object{
        const val MY_ID = 0
    }
}

object TestMessageModel{
    var test = MessageModel(
        messages = listOf(
            Message(
                text = "Hi",
                author = Author(id = 0, name = "Musa")
            ),
            Message(
                text = "Hello!",
                author = Author(id = 1, name = "Musashi")
            ),
            Message(
                text = "Where are you?",
                author = Author(id = 0, name = "Musa")
            ),
            Message(
                text = "I'm home!",
                author = Author(id = 1, name = "Musashi")
            ),
            Message(
                text = "Come here!",
                author = Author(id = 1, name = "Musashi")
            ),
        ),
        receiver = Author(id = 1, name = "Musashi")
    )
    val empty = MessageModel(
        messages = emptyList(),
        receiver = Author(id = 1, name = "Bot")
    )
}