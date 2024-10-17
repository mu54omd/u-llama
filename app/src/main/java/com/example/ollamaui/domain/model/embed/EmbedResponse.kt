package com.example.ollamaui.domain.model.embed

data class EmbedResponse(
    val embeddings: List<List<Double>>,
    val model: String,
)

object EmptyEmbedResponse{
    val empty = EmbedResponse(
        embeddings = listOf(listOf(0.0)),
        model = ""
    )
}
