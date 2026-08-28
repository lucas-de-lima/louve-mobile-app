package com.lucasdelima.louveapp.domain.model

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: UserProfile) : AuthUiState()
    data class Error(val error: AuthError, val retry: () -> Unit) : AuthUiState()
}

sealed class AuthError {
    object NetworkError : AuthError()
    object InvalidCredentials : AuthError()
    object UserCancelled : AuthError()
    data class FirebaseError(val code: String, val message: String) : AuthError()
    data class UnknownError(val message: String) : AuthError()
}