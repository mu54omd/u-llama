package com.example.ollamaui.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ollamaui.domain.model.LogModel

@Database(entities = [LogModel::class], version = 1, exportSchema = true)
abstract class LogDatabase:RoomDatabase() {
    abstract val logDao: LogDao
}