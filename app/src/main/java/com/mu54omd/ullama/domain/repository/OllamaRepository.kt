package com.mu54omd.ullama.domain.repository

import arrow.core.Either
import com.mu54omd.ullama.domain.model.LogModel
import com.mu54omd.ullama.domain.model.NetworkError
import com.mu54omd.ullama.domain.model.chat.ChatInputModel
import com.mu54omd.ullama.domain.model.chat.ChatModel
import com.mu54omd.ullama.domain.model.chat.ChatResponse
import com.mu54omd.ullama.domain.model.embed.EmbedInputModel
import com.mu54omd.ullama.domain.model.embed.EmbedResponse
import com.mu54omd.ullama.domain.model.pull.PullInputModel
import com.mu54omd.ullama.domain.model.pull.PullResponse
import com.mu54omd.ullama.domain.model.tag.TagResponse
import kotlinx.coroutines.flow.Flow

interface OllamaRepository{
    suspend fun getOllamaStatus(baseUrl: String, baseEndpoint: String): Either<NetworkError, String>
    suspend fun getOllamaModelsList(baseUrl: String, tagEndpoint: String): Either<NetworkError, TagResponse>
    suspend fun postOllamaChat(baseUrl: String, chatEndpoint: String, chatInputModel: ChatInputModel?): Flow<Either<NetworkError, ChatResponse>>
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
    suspend fun deleteLogFromDb()
    fun getLogsFromDb(): Flow<List<LogModel>>
}