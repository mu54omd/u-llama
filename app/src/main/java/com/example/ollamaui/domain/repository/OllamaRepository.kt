package com.example.ollamaui.domain.repository

import arrow.core.Either
import com.example.ollamaui.domain.model.LogModel
import com.example.ollamaui.domain.model.chat.ChatInputModel
import com.example.ollamaui.domain.model.chat.ChatModel
import com.example.ollamaui.domain.model.chat.ChatResponse
import com.example.ollamaui.domain.model.embed.EmbedInputModel
import com.example.ollamaui.domain.model.embed.EmbedResponse
import com.example.ollamaui.domain.model.NetworkError
import com.example.ollamaui.domain.model.pull.PullInputModel
import com.example.ollamaui.domain.model.pull.PullResponse
import com.example.ollamaui.domain.model.tag.TagResponse
import kotlinx.coroutines.flow.Flow

interface OllamaRepository{
    suspend fun getOllamaStatus(baseUrl: String, baseEndpoint: String): Either<NetworkError, String>
    suspend fun getOllamaModelsList(baseUrl: String, tagEndpoint: String): Either<NetworkError, TagResponse>
    suspend fun postOllamaChat(baseUrl: String, chatEndpoint: String, chatInputModel: ChatInputModel?): Either<NetworkError, ChatResponse>
    suspend fun postOllamaEmbed(baseUrl: String, embedEndpoint: String, embedInputModel: EmbedInputModel): Either<NetworkError, EmbedResponse>
    suspend fun postOllamaPull(baseUrl: String, pullEndpoint: String, pullInputModel: PullInputModel): Either<NetworkError, PullResponse>

    suspend fun insertToDb(chatModel: ChatModel)
    suspend fun deleteFromDb(chatModel: ChatModel)
    suspend fun deleteFromDbById(chatId: Int)
    suspend fun updateDbItem(chatModel: ChatModel)
    fun getChats(): Flow<List<ChatModel>>
    suspend fun getChat(chatId: Int): ChatModel?
    suspend fun getLastId(): Int

    suspend fun insertLogToDb(logModel: LogModel)
    suspend fun deleteLogFromDb(logModel: LogModel)
    fun getLogsFromDb(): Flow<List<LogModel>>
}