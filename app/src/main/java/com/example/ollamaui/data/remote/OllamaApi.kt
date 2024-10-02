package com.example.ollamaui.data.remote

import com.example.ollamaui.domain.model.ChatInputModel
import com.example.ollamaui.domain.model.ChatResponse
import com.example.ollamaui.domain.model.TagResponse
import com.example.ollamaui.utils.Constants.OLLAMA_BASE_ENDPOINT
import com.example.ollamaui.utils.Constants.OLLAMA_BASE_URL
import com.example.ollamaui.utils.Constants.OLLAMA_CHAT_ENDPOINT
import com.example.ollamaui.utils.Constants.OLLAMA_LIST_ENDPOINT
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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