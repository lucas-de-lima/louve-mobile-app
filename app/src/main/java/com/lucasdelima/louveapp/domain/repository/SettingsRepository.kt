package com.lucasdelima.louveapp.domain.repository

import kotlinx.coroutines.flow.Flow
import com.lucasdelima.louveapp.domain.model.Result

interface SettingsRepository {
    val theme: Flow<String> // Um Flow para "escutar" mudanças no tema em tempo real
    suspend fun saveTheme(themeName: String)
    
    /**
     * Sincroniza configurações da nuvem para o local quando o usuário volta a ficar online.
     */
    suspend fun syncWhenOnline(): Result<Unit>
}