package com.example.ollamaui.domain.model.pull

import kotlinx.serialization.Serializable

@Serializable
data class PullInputModel(
    val name: String,
    val stream: Boolean = false
)
