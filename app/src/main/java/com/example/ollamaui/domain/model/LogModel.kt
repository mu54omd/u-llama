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
object DumbLogModel{
    val dumb = LogModel(
        logId = 0,
        date = "2025-02-07T03:19:11.620",
        type = "ollama-post",
        content = "post: http://192.168.1.103:11434/api/chat"
    )
}
