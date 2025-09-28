package com.lucasdelima.louveapp.domain.repository

import kotlinx.coroutines.flow.Flow
import com.lucasdelima.louveapp.domain.model.Result

interface SettingsRepository {
    val theme: Flow<String> // Um Flow para "escutar" mudanças no tema em tempo real
    suspend fun saveTheme(themeName: String)
    
    /**
     * Obtém o fator de escala da fonte persistido.
     * @return Flow<Float> com o valor atual do fontScaleFactor (padrão: 1.0f)
     */
    val fontScaleFactor: Flow<Float>
    
    /**
     * Salva o fator de escala da fonte.
     * @param factor Valor do fontScaleFactor (deve estar entre 0.5f e 2.0f)
     */
    suspend fun saveFontScaleFactor(factor: Float)
    
    /**
     * Sincroniza configurações da nuvem para o local quando o usuário volta a ficar online.
     */
    suspend fun syncWhenOnline(): Result<Unit>
}