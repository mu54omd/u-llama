package com.example.ollamaui.domain.model.chat

import com.example.ollamaui.domain.model.MessageModel
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    @SerializedName("created_at")
    @SerialName("created_at")
    val createdAt: String,
    val done: Boolean,
    @SerializedName("done_reason")
    @SerialName("done_reason")
    val doneReason: String? = null,
    val model: String,
    val message: MessageModel,
    @SerializedName("total_duration")
    @SerialName("total_duration")
    val totalDuration: Long? = null
)

object EmptyChatResponse{
    val empty = ChatResponse(
        createdAt = "",
        done = false,
        doneReason = "",
        model = "",
        message = MessageModel(role = "", content = "", date = "", time = ""),
        totalDuration = 0L
    )
}