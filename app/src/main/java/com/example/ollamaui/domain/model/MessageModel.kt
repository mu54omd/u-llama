package com.example.ollamaui.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageModel(
    val content: String,
    val role: String,
    val images: List<String>? = null
)
