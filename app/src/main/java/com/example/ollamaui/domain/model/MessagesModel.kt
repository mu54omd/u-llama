package com.example.ollamaui.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class MessagesModel(
    val messageModels: List<MessageModel>,
)

object EmptyMessageModel{
    val empty = MessagesModel(
        messageModels = emptyList(),
    )
}