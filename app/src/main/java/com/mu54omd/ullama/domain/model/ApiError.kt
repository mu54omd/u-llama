package com.mu54omd.ullama.domain.model

import com.mu54omd.ullama.R

enum class ApiError(val message: Int){
    NetworkError(R.string.network_error),
    UnknownError(R.string.unknown_error),
    UnknownResponse(R.string.unknown_response)
}