package com.lucasdelima.louveapp.ui.common.state

sealed class UiState {
    object Loading : UiState()
    data class Success<T>(val data: T) : UiState()
    data class Error(val throwable: Throwable) : UiState()
    
    // Caso específico para o tema principal do app
    data class ThemeSuccess(val themeName: String) : UiState()
}