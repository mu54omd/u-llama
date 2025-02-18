package com.example.ollamaui.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Immutable
@Serializable
data class MessageModel(
    val content: String,
    val role: String,
    val time: String = LocalTime.now().toString(),
    val date: String = LocalDate.now().toString(),
    val images: List<String>? = null,
    val messageId: String = UUID.randomUUID().toString()
)
