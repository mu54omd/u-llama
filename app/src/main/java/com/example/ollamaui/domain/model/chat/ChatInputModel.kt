package com.example.ollamaui.domain.model.chat

import com.example.ollamaui.domain.model.MessageModel
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ChatInputModel(
    val model: String,
    val messages: List<MessageModel>,
    val stream: Boolean,
    @SerializedName("keep_alive")
    val keepAlive: Int,
    val options: ModelParameters = DefaultModelParameters.default
)