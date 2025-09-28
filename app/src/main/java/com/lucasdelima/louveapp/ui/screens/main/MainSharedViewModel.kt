package com.lucasdelima.louveapp.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ✅ OTIMIZAÇÃO: ViewModel compartilhado para dados comuns entre MainScreen e telas individuais
 * Elimina ViewModels duplicados e melhora performance de navegação
 */
@HiltViewModel
class MainSharedViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    /**
     * Perfil do usuário atual - observado por todas as telas que precisam desta informação
     */
    val userProfile: StateFlow<UserProfile?> = authRepository.getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000), // ✅ OTIMIZAÇÃO: Timeout reduzido
            initialValue = null
        )
    
    /**
     * Tema atual - observado por todas as telas que precisam desta informação
     */
    val currentTheme: StateFlow<String> = settingsRepository.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000), // ✅ OTIMIZAÇÃO: Timeout reduzido
            initialValue = "Padrão Claro"
        )
    
    /**
     * Função para selecionar tema - centralizada para evitar duplicação
     */
    fun selectTheme(themeName: String) {
        viewModelScope.launch {
            settingsRepository.saveTheme(themeName)
        }
    }
}
