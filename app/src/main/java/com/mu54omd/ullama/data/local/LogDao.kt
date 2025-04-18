package com.mu54omd.ullama.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mu54omd.ullama.domain.model.LogModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Insert
    suspend fun insert(logModel: LogModel)
    @Query("DELETE FROM LogModel")
    suspend fun delete()
    @Query("SELECT * FROM LOGMODEL")
    fun getLogs(): Flow<List<LogModel>>
}