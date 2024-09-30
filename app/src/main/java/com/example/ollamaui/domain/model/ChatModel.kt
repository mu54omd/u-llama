package com.example.ollamaui.domain.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.absoluteValue
import kotlin.random.Random

@Entity
data class ChatModel(
    @PrimaryKey
    val chatId: Int,
    val modelName: String,
    val chatIcon: Int,
    val chatTitle: String,
    val context: List<Int>,
    val chatMessages: MessageModel,
)


object EmptyChatModel {
    val empty = ChatModel(
        chatId = 0,
        modelName = "",
        chatMessages = TestMessageModel.empty,
        chatIcon = 0,
        chatTitle = "",
        context = listOf(Random.nextInt().absoluteValue, Random.nextInt().absoluteValue)
    )
}
