package com.example.ollamaui.domain.model.embed

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