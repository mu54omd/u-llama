package com.example.ollamaui.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatModel(
    @PrimaryKey(autoGenerate = true)
    val chatId: Int = 0,
    val modelName: String,
    val chatIcon: Int,
    val yourName: String,
    val chatTitle: String,
    val context: List<Int>,
    val chatMessages: MessagesModel,
)


object EmptyChatModel {
    val empty = ChatModel(
        chatId = 0,
        modelName = "",
        yourName = "",
        chatMessages = TestMessageModel.empty,
        chatIcon = 0,
        chatTitle = "",
        context = emptyList()
    )
}
