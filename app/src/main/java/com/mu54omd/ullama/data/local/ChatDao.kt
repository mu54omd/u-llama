package com.mu54omd.ullama.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mu54omd.ullama.domain.model.chat.ChatModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(chatModel: ChatModel)

    @Update
    suspend fun update(chatModel: ChatModel)

    @Delete
    suspend fun delete(chatModel: ChatModel)

    @Query("DELETE FROM ChatModel WHERE (chatId=:chatId)")
    suspend fun deleteById(chatId: Int)

    @Query("SELECT * FROM ChatModel")
    fun getChats(): Flow<List<ChatModel>>

    @Query("SELECT * FROM ChatModel WHERE (chatId=:chatId) ")
    suspend fun getChat(chatId: Int): ChatModel?

    @Query("SELECT chatId FROM ChatModel ORDER BY chatId DESC LIMIT 1")
    suspend fun getLastId(): Int
}