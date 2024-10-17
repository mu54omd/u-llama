package com.example.ollamaui.domain.model.tag

data class TagResponse(
    val models: List<TagModel>
)

object EmptyTagResponse{
    val emptyTagResponse = TagResponse(models = emptyList<TagModel>())
}