package com.example.ollamaui.domain.preferences

import kotlinx.coroutines.flow.Flow

interface LocalUserManager{
    suspend fun saveOllamaUrl(url: String)
    fun readOllamaUrl(): Flow<String>
    fun readOllamaAddressStatus():Flow<Boolean>
}