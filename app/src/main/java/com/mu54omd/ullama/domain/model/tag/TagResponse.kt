package com.mu54omd.ullama.domain.model.tag

data class TagResponse(
    val models: List<TagModel>
)

object EmptyTagResponse{
    val emptyTagResponse = TagResponse(models = emptyList<TagModel>())
}