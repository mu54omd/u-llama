package com.example.ollamaui.domain.model.chat

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ModelParameters(
    val temperature: Float = 0.0f,
    @SerializedName("num_ctx")
    val numCtx: Int = 1,
    @SerializedName("presence_penalty")
    val presencePenalty: Float = 0.0f,
    @SerializedName("frequency_penalty")
    val frequencyPenalty: Float = 0.0f,
    @SerializedName("top_k")
    val topK: Int = 0,
    @SerializedName("top_p")
    val topP: Float = 0.0f,
    @SerializedName("min_p")
    val minP: Float = 0.0f,
)

object DefaultModelParameters {
    val default = ModelParameters(
        topK = 40,
        topP = 0.9f,
        minP = 0.0f,
        temperature = 0.8f,
        presencePenalty = 1.5f,
        frequencyPenalty = 1.0f,
        numCtx = 2048
    )
}
