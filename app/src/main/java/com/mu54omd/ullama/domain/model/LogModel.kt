package com.mu54omd.ullama.domain.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity
data class LogModel(
    @PrimaryKey(autoGenerate = true)
    val logId: Int = 0,
    val date: String,
    val type: String,
    val content: String,
)
