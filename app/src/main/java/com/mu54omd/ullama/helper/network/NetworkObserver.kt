package com.mu54omd.ullama.helper.network

import com.mu54omd.ullama.domain.model.LogModel
import com.mu54omd.ullama.domain.repository.OllamaRepository
import com.mu54omd.ullama.utils.Constants.OLLAMA_IS_RUNNING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class NetworkObserver(
    private val ollamaRepository: OllamaRepository
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .build()

    fun observeConnectivity(url: String): Flow<NetworkStatus> = flow {
        while (true){
            val networkResponse = checkConnectivity(url)
            emit(if(networkResponse) NetworkStatus.CONNECTED else NetworkStatus.DISCONNECTED)
            delay(1000)
        }
    }.distinctUntilChanged()
    private suspend fun checkConnectivity(url: String): Boolean{
        return try {
            withContext(Dispatchers.IO) {
                val request = Request.Builder().url(url).get().build()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                response.isSuccessful && responseBody?.contains(OLLAMA_IS_RUNNING) == true
            }
        }catch (e: Exception){
            ollamaRepository.insertLogToDb(
                LogModel(
                    date = LocalDateTime.now().toString(),
                    type = "ERROR",
                    content = "network status: ${e.message}",
                )
            )
            false
        }
    }
}