package com.mu54omd.ullama.mapper

import com.mu54omd.ullama.domain.model.ApiError
import com.mu54omd.ullama.domain.model.NetworkError
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toNetworkError(): NetworkError {
    val error = when(this){
        is IOException -> ApiError.NetworkError
        is HttpException -> ApiError.UnknownResponse
        else -> ApiError.UnknownError
    }
    return NetworkError(error = error, t = this)
}