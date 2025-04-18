package com.mu54omd.ullama.domain.model

data class NetworkError(
    val error: ApiError,
    val t: Throwable
)