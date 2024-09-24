package com.example.ollamaui.data.remote

import com.example.ollamaui.utils.Constants.OLLAMA_BASE_ENDPOINT
import com.example.ollamaui.utils.Constants.OLLAMA_CHAT_ENDPOINT
import retrofit2.http.GET
import retrofit2.http.POST


interface OllamaApi {
    @GET(OLLAMA_BASE_ENDPOINT)
    suspend fun ollamaState():String

//    @POST(OLLAMA_CHAT_ENDPOINT)
//    suspend fun ollamaChat()
}