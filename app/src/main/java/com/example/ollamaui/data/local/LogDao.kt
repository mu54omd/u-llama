package com.example.ollamaui.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.ollamaui.domain.model.LogModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert
    suspend fun insert(logModel: LogModel)
    @Delete
    suspend fun delete(logModel: LogModel)
    @Query("SELECT * FROM LOGMODEL")
    fun getLogs(): Flow<List<LogModel>>
}