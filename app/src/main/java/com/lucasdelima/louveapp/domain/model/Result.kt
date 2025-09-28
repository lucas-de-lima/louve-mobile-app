package com.lucasdelima.louveapp.domain.model

/**
 *
 * Uma classe wrapper genérica para representar o resultado de operações assíncronas,
 * encapsulando tanto o sucesso quanto a falha. Essencial para o Repository Pattern.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val cause: Exception? = null) : Result<Nothing>()
}
