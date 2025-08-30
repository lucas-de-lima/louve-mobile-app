package com.lucasdelima.louveapp.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Define o contrato para gerenciar configurações salvas localmente no dispositivo.
 */
interface LocalSettingsRepository {
    val theme: Flow<String>
    suspend fun saveTheme(themeName: String)
}
