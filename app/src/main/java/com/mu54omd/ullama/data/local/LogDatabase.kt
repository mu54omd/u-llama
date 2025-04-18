package com.mu54omd.ullama.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mu54omd.ullama.domain.model.LogModel

@Database(entities = [LogModel::class], version = 1, exportSchema = true)
abstract class LogDatabase:RoomDatabase() {
    abstract val logDao: LogDao
}