package com.example.ollamaui.domain.model.objectbox

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class File(
    @Id var fileId: Long = 0,
    var chatId: Int = 0,
    var hash: String = "",
    var attachResult: String = "",
    var fileName: String = "",
    var fileType: String = "",
    var fileAddedTime: Long = 0,
    var isImage: Boolean = false
)
