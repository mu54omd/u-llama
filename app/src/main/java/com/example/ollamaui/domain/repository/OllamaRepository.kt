package com.example.ollamaui.domain.repository

import arrow.core.Either
import com.example.ollamaui.data.remote.OllamaApi
import com.example.ollamaui.domain.model.NetworkError

interface OllamaRepository{
    suspend fun getOllamaStatus(): Either<NetworkError, String>
}