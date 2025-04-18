package com.mu54omd.ullama.domain.preferences

import com.mu54omd.ullama.activity.BaseAddress
import com.mu54omd.ullama.activity.EmbeddingModel
import com.mu54omd.ullama.domain.model.chat.ModelParameters
import kotlinx.coroutines.flow.Flow

interface LocalUserManager{
    suspend fun saveOllamaUrl(url: String)
    fun readOllamaUrl(): Flow<BaseAddress>
    suspend fun saveOllamaEmbeddingModel(modelName: String)
    fun readOllamaEmbeddingModel(): Flow<EmbeddingModel>
    suspend fun saveTuningParameters(modelParameters: ModelParameters)
    fun readTuningParameters(): Flow<ModelParameters>
}