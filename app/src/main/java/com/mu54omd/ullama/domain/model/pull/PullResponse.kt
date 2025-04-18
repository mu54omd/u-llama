package com.mu54omd.ullama.domain.model.pull

data class PullResponse(
    val status: String
)

object EmptyPullResponse{
    val empty = PullResponse(
        status = ""
    )
}
