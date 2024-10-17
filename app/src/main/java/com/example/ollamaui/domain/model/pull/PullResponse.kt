package com.example.ollamaui.domain.model.pull

data class PullResponse(
    val status: String
)

object EmptyPullResponse{
    val empty = PullResponse(
        status = ""
    )
}
