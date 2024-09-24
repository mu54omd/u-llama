package com.example.ollamaui.domain.model

import com.example.ollamaui.R

enum class ApiError(val message: Int){
    NetworkError(R.string.network_error),
    UnknownError(R.string.unknown_error),
    UnknownResponse(R.string.unknown_response)
}