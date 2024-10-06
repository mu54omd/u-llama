package com.example.ollamaui.domain.repository

import arrow.core.Either
import com.example.ollamaui.domain.model.ChatInputModel
import com.example.ollamaui.domain.model.ChatModel
import com.example.ollamaui.domain.model.ChatResponse
import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.domain.model.TagResponse
import kotlinx.coroutines.flow.Flow

interface OllamaRepository{
    suspend fun getOllamaStatus(baseUrl: String, baseEndpoint: String): Either<NetworkError, String>
    suspend fun getOllamaModelsList(baseUrl: String, tagEndpoint: String): Either<NetworkError, TagResponse>
    suspend fun postOllamaChat(baseUrl: String, chatEndpoint: String, chatInputModel: ChatInputModel?): Either<NetworkError, ChatResponse>

    suspend fun insertToDb(chatModel: ChatModel)
    suspend fun deleteFromDb(chatModel: ChatModel)
    suspend fun updateDbItem(chatModel: ChatModel)
    fun getChats(): Flow<List<ChatModel>>
    suspend fun getChat(chatId: Int): ChatModel?
    suspend fun getLastId(): Int
}