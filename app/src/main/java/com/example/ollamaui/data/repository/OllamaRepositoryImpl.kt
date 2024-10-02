package com.example.ollamaui.data.repository

import arrow.core.Either
import com.example.ollamaui.data.local.ChatDao
import com.example.ollamaui.data.remote.OllamaApi
import com.example.ollamaui.domain.model.ChatInputModel
import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.ChatResponse
import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.domain.model.TagResponse
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.mapper.toNetworkError
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OllamaRepositoryImpl @Inject constructor(
    private val ollamaApi: OllamaApi,
    private val chatDao: ChatDao
): OllamaRepository {
    override suspend fun getOllamaStatus(baseUrl: String, baseEndpoint: String): Either<NetworkError, String> {
        return Either.catch { ollamaApi.ollamaState(fullUrl = baseUrl + baseEndpoint) }.mapLeft { it.toNetworkError() }
    }

    override suspend fun getOllamaModelsList(baseUrl: String, tagEndpoint: String): Either<NetworkError, TagResponse> {
        return Either.catch { ollamaApi.ollamaModelsList(fullUrl = baseUrl + tagEndpoint) }.mapLeft { it.toNetworkError() }
    }

    override suspend fun postOllamaChat(
        baseUrl: String, chatEndpoint: String,
        chatInputModel: ChatInputModel?
    ): Either<NetworkError, ChatResponse> {
        return Either.catch { ollamaApi.ollamaChat(fullUrl = baseUrl + chatEndpoint, chatInputModel = chatInputModel) }.mapLeft { it.toNetworkError() }
    }

    override suspend fun insertToDb(chatModel: ChatModel) {
        chatDao.insert(chatModel)
    }

    override suspend fun deleteFromDb(chatModel: ChatModel) {
        chatDao.delete(chatModel)
    }

    override suspend fun updateDbItem(chatModel: ChatModel) {
        chatDao.update(chatModel)
    }

    override fun getChats(): Flow<List<ChatModel>> {
        return chatDao.getChats()
    }

    override suspend fun getChat(chatId: Int): ChatModel? {
        return chatDao.getChat(chatId)
    }
}