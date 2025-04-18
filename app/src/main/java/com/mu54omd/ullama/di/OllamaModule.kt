package com.mu54omd.ullama.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.mu54omd.ullama.data.local.ChatDao
import com.mu54omd.ullama.data.local.ChatDatabase
import com.mu54omd.ullama.data.local.LogDao
import com.mu54omd.ullama.data.local.LogDatabase
import com.mu54omd.ullama.data.preferences.LocalUserManagerImpl
import com.mu54omd.ullama.data.remote.OllamaApi
import com.mu54omd.ullama.data.repository.OllamaRepositoryImpl
import com.mu54omd.ullama.domain.preferences.LocalUserManager
import com.mu54omd.ullama.domain.repository.OllamaRepository
import com.mu54omd.ullama.utils.Constants.CHAT_DATABASE
import com.mu54omd.ullama.utils.Constants.LOG_DATABASE
import com.mu54omd.ullama.utils.Constants.OLLAMA_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideLocalUserManager(@ApplicationContext context: Context):LocalUserManager{
        return LocalUserManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideOllamaApi():OllamaApi{
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val okHttpClient =  OkHttpClient.Builder().apply {
            this.addInterceptor(
                HttpLoggingInterceptor()
                .apply { this.level = HttpLoggingInterceptor.Level.BASIC }
            )
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5,TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
        }
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(OLLAMA_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(OllamaApi::class.java)
    }

    //Provide Chat Database for Hilt
    @Provides
    @Singleton
    fun provideChatDatabase( application: Application): ChatDatabase {
        return Room.databaseBuilder(
                context = application,
                klass = ChatDatabase::class.java,
                name = CHAT_DATABASE
            ).fallbackToDestructiveMigration(false)
            .build()
    }

    //Provide Chat Dao for Hilt
    @Provides
    @Singleton
    fun provideChatDao(
        chatDatabase: ChatDatabase
    ): ChatDao{
        return chatDatabase.chatDao
    }

    //Provide Log Database for Hilt
    @Provides
    @Singleton
    fun provideLogDatabase( application: Application): LogDatabase {
        return Room.databaseBuilder(
                context = application,
                klass = LogDatabase::class.java,
                name = LOG_DATABASE
            ).fallbackToDestructiveMigration(false)
            .build()
    }

    //Provide Log Dao for Hilt
    @Provides
    @Singleton
    fun provideLogDao(
        logDatabase: LogDatabase
    ): LogDao {
        return logDatabase.logDao
    }



    @Provides
    @Singleton
    fun provideOllamaRepository(
        ollamaApi: OllamaApi,
        chatDao: ChatDao,
        logDao: LogDao
    ): OllamaRepository{
        return OllamaRepositoryImpl(
            ollamaApi = ollamaApi,
            chatDao = chatDao,
            logDao = logDao
        )
    }

}