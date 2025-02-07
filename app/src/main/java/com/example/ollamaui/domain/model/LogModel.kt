package com.example.ollamaui.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LogModel(
    @PrimaryKey(autoGenerate = true)
    val logId: Int = 0,
    val date: String,
    val type: String,
    val content: String,
)
