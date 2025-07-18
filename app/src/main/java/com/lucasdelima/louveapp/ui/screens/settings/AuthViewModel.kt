package com.lucasdelima.louveapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.repository.AuthCredentials
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    /**
     * Expõe o perfil do usuário atual como um StateFlow.
     * A UI irá observar este estado para saber se o usuário está logado ou não.
     */
    val userProfile: StateFlow<UserProfile?> = authRepository.getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Começa como nulo até o primeiro valor ser emitido
        )

    /**
     * Tenta iniciar o processo de login com a credencial especificada.
     * Por enquanto, só lidaremos com o Google.
     */
    fun signIn() {
        viewModelScope.launch {
            // A lógica real do fluxo de UI do Login com Google será adicionada aqui.
            // Por enquanto, chamamos o repositório.
            authRepository.signIn(AuthCredentials.Google)
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
}
