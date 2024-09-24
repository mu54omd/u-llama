package com.example.ollamaui.domain.model

data class NetworkError(
    val error: ApiError,
    val t: Throwable
)