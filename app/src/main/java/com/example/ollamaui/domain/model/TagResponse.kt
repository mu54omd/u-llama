package com.example.ollamaui.domain.model

data class TagResponse(
    val models: List<TagModel>
)

object EmptyTagResponse{
    val emptyTagResponse = TagResponse(models = emptyList<TagModel>())
}