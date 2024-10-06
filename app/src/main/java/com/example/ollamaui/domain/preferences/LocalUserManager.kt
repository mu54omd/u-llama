package com.example.ollamaui.domain.preferences

import com.example.ollamaui.activity.BaseAddress
import kotlinx.coroutines.flow.Flow

interface LocalUserManager{
    suspend fun saveOllamaUrl(url: String)
    fun readOllamaUrl(): Flow<BaseAddress>
}