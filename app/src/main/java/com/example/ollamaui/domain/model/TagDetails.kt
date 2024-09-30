package com.example.ollamaui.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class TagDetails(
    val families: List<String>,
    val family: String,
    val format: String,
    @SerializedName("parameter_size")
    val parameterSize: String,
    @SerializedName("parent_model")
    val parentModel: String,
    @SerializedName("quantization_level")
    val quantizationLevel: String
)

object EmptyTagDetails{
    val emptyTagDetails = TagDetails(
        families = emptyList(),
        family = "",
        format = "",
        parameterSize = "",
        parentModel = "",
        quantizationLevel = ""
    )
}