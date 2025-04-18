package com.mu54omd.ullama.domain.model.chat

import com.mu54omd.ullama.domain.model.MessageModel
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