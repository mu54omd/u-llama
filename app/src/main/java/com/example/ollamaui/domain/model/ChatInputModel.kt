package com.example.ollamaui.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ChatInputModel(
    val model: String,
    val messages: List<MessageModel>,
    val stream: Boolean,
    @SerializedName("keep_alive")
    val keepAlive: Int,
)