package com.mu54omd.ullama.domain.model.embed

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class EmbedInputModel(
    val model: String,
    val input: List<String>? = null,
    @SerializedName("keep_alive")
    val keepAlive: Int = 3600
)