package com.example.ollamaui.data.remote

import com.example.ollamaui.domain.model.ChatInputModel
import com.example.ollamaui.domain.model.ChatResponse
import com.example.ollamaui.domain.model.TagModel
import com.example.ollamaui.domain.model.TagResponse
import com.example.ollamaui.utils.Constants.OLLAMA_BASE_ENDPOINT
import com.example.ollamaui.utils.Constants.OLLAMA_CHAT_ENDPOINT
import com.example.ollamaui.utils.Constants.OLLAMA_LIST_ENDPOINT
import retrofit2.http.Body

import retrofit2.http.GET
import retrofit2.http.POST


interface OllamaApi {
    @GET(OLLAMA_BASE_ENDPOINT)
    suspend fun ollamaState():String

    @GET(OLLAMA_LIST_ENDPOINT)
    suspend fun ollamaModelsList():TagResponse

    @POST(OLLAMA_CHAT_ENDPOINT)
    suspend fun ollamaChat(
        @Body chatInputModel: ChatInputModel?
    ):ChatResponse
}