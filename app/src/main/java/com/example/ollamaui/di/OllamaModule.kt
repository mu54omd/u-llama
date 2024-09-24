package com.example.ollamaui.di

import com.example.ollamaui.data.remote.OllamaApi
import com.example.ollamaui.data.repository.OllamaRepositoryImpl
import com.example.ollamaui.domain.repository.OllamaRepository
import com.example.ollamaui.utils.Constants.OLLAMA_BASE_URL
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OllamaModule {

    @Provides
    @Singleton
    fun provideOllamaApi():OllamaApi{
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val okHttpClient =  OkHttpClient.Builder().apply {
            this.addInterceptor(HttpLoggingInterceptor().apply { this.level = HttpLoggingInterceptor.Level.BODY })
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
        }
            .build()
        return Retrofit.Builder()
            .baseUrl(OLLAMA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(OllamaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOllamaRepository(
        ollamaApi: OllamaApi
    ): OllamaRepository{
        return OllamaRepositoryImpl(
            ollamaApi = ollamaApi
        )
    }

}