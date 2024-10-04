package com.example.ollamaui.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatModel(
    @PrimaryKey(autoGenerate = true)
    val databaseId: Int = 0,
    val chatId: Int,
    val modelName: String,
    val chatIcon: Int,
    val yourName: String,
    val chatTitle: String,
    val context: List<Int>,
    val chatMessages: MessageModel,
)


object EmptyChatModel {
    val empty = ChatModel(
        databaseId = 0,
        chatId = 0,
        modelName = "",
        yourName = "",
        chatMessages = TestMessageModel.empty,
        chatIcon = 0,
        chatTitle = "",
        context = emptyList()
    )
}
