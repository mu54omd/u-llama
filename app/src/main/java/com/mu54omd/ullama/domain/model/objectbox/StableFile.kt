package com.mu54omd.ullama.domain.model.objectbox

data class StableFile(
    val fileId: Long = 0,
    val hash: String = "",
    val attachResult: String = "",
    val fileName: String = "",
    val fileType: String = "",
    val fileAddedTime: Long = 0,
    val isImage: Boolean = false
)

//fun StableFile.toFile() = File(
//    fileId = fileId,
//    hash = hash,
//    attachResult = attachResult,
//    fileName = fileName,
//    fileType = fileType,
//    fileAddedTime = fileAddedTime,
//    isImage = isImage
//)
