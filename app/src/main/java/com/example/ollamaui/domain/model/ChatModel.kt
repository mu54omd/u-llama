package com.example.ollamaui.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatModel(
    @PrimaryKey(autoGenerate = true)
    val chatId: Int = 0,
    val modelName: String,
    val chatIcon: Int,
    val userName: String,
    val botName: String,
    val chatTitle: String,
    val chatMessages: MessagesModel,
    val newMessageStatus: Int = 0,
)


object EmptyChatModel {
    val empty = ChatModel(
        chatId = 0,
        modelName = "",
        userName = "",
        botName = "",
        chatMessages = EmptyMessageModel.empty,
        chatIcon = 0,
        chatTitle = "",
        newMessageStatus = 0
    )
}
