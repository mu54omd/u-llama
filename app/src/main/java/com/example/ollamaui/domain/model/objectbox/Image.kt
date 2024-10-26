package com.example.ollamaui.domain.model.objectbox

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Image(
    @Id var imageId: Long = 0,
    var chatId: Int = 0,
    var imageBase64: String = "",
    var imageFileName: String = "",
    var imageAddedTime: Long = 0,
)
