package com.example.ollamaui.domain.model.objectbox

data class QueryResult(
    val response: String,
    val context: List<RetrievedContext>
)
