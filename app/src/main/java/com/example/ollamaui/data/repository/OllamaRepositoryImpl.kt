package com.example.ollamaui.data.repository

import arrow.core.Either
import com.example.ollamaui.data.remote.OllamaApi
import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.mapper.toNetworkError
import javax.inject.Inject

class OllamaRepositoryImpl @Inject constructor(
    private val ollamaApi: OllamaApi
): OllamaRepository {
    override suspend fun getOllamaStatus(): Either<NetworkError, String> {
        return Either.catch { ollamaApi.ollamaState() }.mapLeft { it.toNetworkError() }
    }

}