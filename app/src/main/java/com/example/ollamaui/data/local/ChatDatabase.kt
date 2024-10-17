package com.example.ollamaui.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ollamaui.domain.model.chat.ChatModel

@Database(entities = [ChatModel::class], version = 2, exportSchema = true)
@TypeConverters(value = [ChatConverter::class])
abstract class ChatDatabase:RoomDatabase() {
    abstract val chatDao: ChatDao
}