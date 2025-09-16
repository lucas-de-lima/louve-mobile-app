package com.lucasdelima.louveapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {
    
    /**
     * ✅ SOLUÇÃO SIMPLES: Expõe diretamente o tema atual
     * Observa mudanças em tempo real automaticamente
     */
    val currentTheme: StateFlow<String> = settingsRepository.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000), // ✅ OTIMIZAÇÃO: Reduzido de 5000 para 1000ms
            initialValue = "Padrão Claro"
        )
}