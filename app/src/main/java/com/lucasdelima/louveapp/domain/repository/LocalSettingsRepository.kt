package com.lucasdelima.louveapp.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Define o contrato para gerenciar configurações salvas localmente no dispositivo.
 */
interface LocalSettingsRepository {
    val theme: Flow<String>
    suspend fun saveTheme(themeName: String)
    
    /**
     * Obtém o fator de escala da fonte persistido localmente.
     * @return Flow<Float> com o valor atual do fontScaleFactor (padrão: 1.0f)
     */
    val fontScaleFactor: Flow<Float>
    
    /**
     * Salva o fator de escala da fonte localmente.
     * @param factor Valor do fontScaleFactor (deve estar entre 0.5f e 2.0f)
     */
    suspend fun saveFontScaleFactor(factor: Float)
}
