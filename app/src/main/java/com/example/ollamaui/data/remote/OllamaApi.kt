package com.example.ollamaui.data.remote

import com.example.ollamaui.domain.model.chat.ChatInputModel
import com.example.ollamaui.domain.model.chat.ChatResponse
import com.example.ollamaui.domain.model.embed.EmbedInputModel
import com.example.ollamaui.domain.model.embed.EmbedResponse
import com.example.ollamaui.domain.model.pull.PullInputModel
import com.example.ollamaui.domain.model.pull.PullResponse
import com.example.ollamaui.domain.model.tag.TagResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    //Post chat dialog
    @POST
    suspend fun ollamaChat(
        @Url fullUrl: String,
        @Body chatInputModel: ChatInputModel?,
    ): ChatResponse

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