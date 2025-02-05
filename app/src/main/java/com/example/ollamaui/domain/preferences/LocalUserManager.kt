package com.example.ollamaui.domain.preferences

import com.example.ollamaui.activity.BaseAddress
import com.example.ollamaui.activity.EmbeddingModel
import com.example.ollamaui.domain.model.chat.ModelParameters
import kotlinx.coroutines.flow.Flow

interface LocalUserManager{
    suspend fun saveOllamaUrl(url: String)
    fun readOllamaUrl(): Flow<BaseAddress>
    suspend fun saveOllamaEmbeddingModel(modelName: String)
    fun readOllamaEmbeddingModel(): Flow<EmbeddingModel>
    suspend fun saveTuningParameters(modelParameters: ModelParameters)
    fun readTuningParameters(): Flow<ModelParameters>
}