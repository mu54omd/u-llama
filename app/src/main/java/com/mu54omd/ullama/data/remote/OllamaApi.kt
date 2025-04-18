package com.mu54omd.ullama.data.remote

import com.mu54omd.ullama.domain.model.chat.ChatInputModel
import com.mu54omd.ullama.domain.model.embed.EmbedInputModel
import com.mu54omd.ullama.domain.model.embed.EmbedResponse
import com.mu54omd.ullama.domain.model.pull.PullInputModel
import com.mu54omd.ullama.domain.model.pull.PullResponse
import com.mu54omd.ullama.domain.model.tag.TagResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming
import retrofit2.http.Url


interface OllamaApi {
    //Get status of ollama
    @GET
    suspend fun ollamaState(
        @Url fullUrl: String,
    ):String

    //Get list of available models
    @GET
    suspend fun ollamaModelsList(
        @Url fullUrl: String
    ): TagResponse

    //Post chat dialog when streaming is enabled
    @POST
    @Streaming
    suspend fun ollamaChat(
        @Url fullUrl: String,
        @Body chatInputModel: ChatInputModel?,
    ): ResponseBody

    //Post embedding request
    @POST
    suspend fun ollamaEmbed(
        @Url fullUrl: String,
        @Body embedInputModel: EmbedInputModel
    ): EmbedResponse

    //Post pull request
    @POST
    suspend fun ollamaPull(
        @Url fullUrl: String,
        @Body pullInputModel: PullInputModel
    ): PullResponse
}