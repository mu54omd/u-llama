package com.example.ollamaui.data.remote

import com.example.ollamaui.domain.model.ChatInputModel
import com.example.ollamaui.domain.model.ChatResponse
import com.example.ollamaui.domain.model.TagResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url


interface OllamaApi {
    @GET
    suspend fun ollamaState(
        @Url fullUrl: String,
    ):String

    @GET
    suspend fun ollamaModelsList(
        @Url fullUrl: String
    ):TagResponse

    @POST
    suspend fun ollamaChat(
        @Url fullUrl: String,
        @Body chatInputModel: ChatInputModel?,
    ):ChatResponse
}