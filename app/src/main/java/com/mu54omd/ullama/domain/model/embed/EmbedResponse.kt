package com.mu54omd.ullama.domain.model.embed

data class EmbedResponse(
    val embeddings: List<FloatArray>,
    val model: String,
)

object EmptyEmbedResponse{
    val empty = EmbedResponse(
        embeddings = listOf(FloatArray(0)),
        model = ""
    )
}