package com.lucasdelima.louveapp.domain.model

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val message: String,
        val cause: Exception? = null,
        val type: ErrorType = ErrorType.UNKNOWN
    ) : Result<Nothing>()
}

enum class ErrorType {
    TRANSIENT,
    ACTIONABLE,
    UNKNOWN
}
