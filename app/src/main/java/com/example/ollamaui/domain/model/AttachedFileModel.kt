package com.example.ollamaui.domain.model

data class AttachedFileModel(
    val id: Long,
    val fileType: String,
    val fileName: String,
    val attachResult: String?,
    val attachError: String?,
    val isImage: Boolean = false
)

object EmptyAttachedFileModel{
    val empty = AttachedFileModel(
        id = 0,
        fileName = "",
        fileType = "",
        attachResult = null,
        attachError = null,
        isImage = false
    )
}
