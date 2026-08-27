package com.lucasdelima.louveapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.domain.model.AuthUiState
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.repository.AuthCredentials
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /** Controle de rate limiting para evitar tentativas muito frequentes */
    private var lastSignInAttempt = 0L
    private val minIntervalBetweenAttempts = 2000L // 2 segundos

    /**
     * Expõe o perfil do usuário atual como um StateFlow.
     * A UI irá observar este estado para saber se o usuário está logado ou não.
     */
    val userProfile: StateFlow<UserProfile?> = authRepository.getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000), // ✅ OTIMIZAÇÃO: Reduzido de 5000 para 1000ms
            initialValue = null // Começa como nulo até o primeiro valor ser emitido
        )

    /**
     * Expõe o estado atual da autenticação como um StateFlow.
     * Permite que a UI reaja aos diferentes estados (loading, error, success).
     */
    val authState: StateFlow<AuthUiState> = authRepository.getAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000), // ✅ OTIMIZAÇÃO: Reduzido de 5000 para 1000ms
            initialValue = AuthUiState.Idle
        )

    /**
     * Tenta realizar o login passando a credencial correta.
     * A função agora espera um objeto AuthCredentials, como definido na nossa interface.
     * Inclui rate limiting para evitar tentativas muito frequentes.
     */
    fun signIn(credentials: AuthCredentials) {
        val now = System.currentTimeMillis()
        if (now - lastSignInAttempt < minIntervalBetweenAttempts) {
            // Rate limiting - não permitir tentativas muito frequentes
            return
        }
        
        lastSignInAttempt = now
        viewModelScope.launch {
            authRepository.signIn(credentials)
        }
    }

    /**
     * Realiza o logout do usuário atual.
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
    
    /**
     * Inicia o processo de login com Google.
     * Esta função deve ser chamada quando o usuário clicar no botão de login.
     * A implementação real do login está na UI que usa o GoogleSignInClient.
     */
    fun signInWithGoogle() {
        // Esta função é um placeholder para o tracking de analytics.
        // A implementação real do login está na UI que usa o GoogleSignInClient.
        // O tracking será feito antes de chamar esta função.
    }

    /**
     * Retry com backoff exponencial para tentativas de login.
     * Implementa uma estratégia de retry inteligente que aumenta o delay entre tentativas.
     * 
     * @param credentials As credenciais para tentar o login novamente
     * @param maxRetries Número máximo de tentativas (padrão: 3)
     */
    fun retrySignIn(credentials: AuthCredentials, maxRetries: Int = 3) {
        viewModelScope.launch {
            var retryCount = 0
            var delay = 1000L // 1 segundo inicial
            
            while (retryCount < maxRetries) {
                when (val result = authRepository.signIn(credentials)) {
                    is Result.Success<*> -> {
                        // Sucesso - sair do loop
                        break
                    }
                    is Result.Error -> {
                        retryCount++
                        if (retryCount < maxRetries) {
                            delay(delay)
                            delay *= 2 // Backoff exponencial
                        }
                    }
                }
            }
        }
    }
}
