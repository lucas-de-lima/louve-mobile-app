package com.lucasdelima.louveapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import com.lucasdelima.louveapp.ui.theme.AllThemes
import com.lucasdelima.louveapp.ui.theme.LouveThemeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Define o estado da UI para a tela de configurações
data class SettingsUiState(
    val availableThemes: List<LouveThemeData> = emptyList(),
    val selectedThemeName: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // Expomos o estado da UI, que é reativo
    val uiState: StateFlow<SettingsUiState> = settingsRepository.theme
        .map { savedThemeName ->
            SettingsUiState(
                availableThemes = AllThemes,
                selectedThemeName = savedThemeName
            )
        }.stateIn( // Converte o Flow em um StateFlow
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState()
        )

    /**
     * Função que a UI chamará quando o usuário selecionar um novo tema.
     */
    fun selectTheme(themeName: String) {
        viewModelScope.launch {
            settingsRepository.saveTheme(themeName)
        }
    }
}