package com.example.ollamaui.domain.model.tag

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class TagModel(
    val tagDetails: TagDetails,
    val digest: String,
    val model: String,
    @SerializedName("modified_at")
    val modifiedAt: String,
    val name: String,
    val size: Long
)

object EmptyTagModel{
    val emptyTagModel = TagModel(
        tagDetails = EmptyTagDetails.emptyTagDetails,
        digest = "",
        model = "",
        modifiedAt = "",
        name = "",
        size = 0L
    )
}