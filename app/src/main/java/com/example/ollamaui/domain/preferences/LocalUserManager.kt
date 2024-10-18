package com.example.ollamaui.domain.preferences

import com.example.ollamaui.activity.BaseAddress
import com.example.ollamaui.activity.EmbeddingModel
import kotlinx.coroutines.flow.Flow

interface LocalUserManager{
    suspend fun saveOllamaUrl(url: String)
    fun readOllamaUrl(): Flow<BaseAddress>
    suspend fun saveOllamaEmbeddingModel(modelName: String)
    fun readOllamaEmbeddingModel(): Flow<EmbeddingModel>
}