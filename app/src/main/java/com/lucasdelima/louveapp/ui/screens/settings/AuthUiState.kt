package com.lucasdelima.louveapp.ui.screens.settings

import com.lucasdelima.louveapp.domain.model.UserProfile

/**
 * Estados da UI para autenticação.
 * Controla o que é exibido na tela baseado no estado atual da autenticação.
 */
sealed class AuthUiState {
    /** Estado inicial - usuário não autenticado */
    object Idle : AuthUiState()
    
    /** Estado de carregamento - autenticação em andamento */
    object Loading : AuthUiState()
    
    /** Estado de sucesso - usuário autenticado com sucesso */
    data class Success(val user: UserProfile) : AuthUiState()
    
    /** Estado de erro - falha na autenticação com opção de retry */
    data class Error(val error: AuthError, val retry: () -> Unit) : AuthUiState()
}

/**
 * Tipos de erro de autenticação.
 * Categoriza os diferentes tipos de falha para melhor tratamento na UI.
 */
sealed class AuthError {
    /** Erro de rede/conectividade */
    object NetworkError : AuthError()
    
    /** Credenciais inválidas ou expiradas */
    object InvalidCredentials : AuthError()
    
    /** Usuário cancelou o processo de login */
    object UserCancelled : AuthError()
    
    /** Erro específico do Firebase com código e mensagem */
    data class FirebaseError(val code: String, val message: String) : AuthError()
    
    /** Erro desconhecido ou não categorizado */
    data class UnknownError(val message: String) : AuthError()
}
