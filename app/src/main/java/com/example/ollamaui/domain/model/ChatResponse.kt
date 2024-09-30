package com.example.ollamaui.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val context: List<Int>,
    @SerializedName("created_at")
    val createdAt: String,
    val done: Boolean,
    @SerializedName("done_reason")
    val doneReason: String,
    val model: String,
    val response: String,
    @SerializedName("total_duration")
    val totalDuration: Long
)

object EmptyChatResponse{
    val empty = ChatResponse(
        context = emptyList(),
        createdAt = "",
        done = false,
        doneReason = "",
        model = "",
        response = "",
        totalDuration = 0L
    )
}