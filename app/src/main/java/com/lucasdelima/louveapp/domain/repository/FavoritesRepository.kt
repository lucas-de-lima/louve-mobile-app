package com.lucasdelima.louveapp.domain.repository

/**
 *
 * Esta é a interface principal que a camada de UI (ViewModel) usará.
 * Ela abstrai completamente se os favoritos estão sendo buscados localmente ou remotamente.
 */

import com.lucasdelima.louveapp.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavoriteHymnIds(): Flow<Result<Set<String>>>
    suspend fun addFavorite(hymnId: String): Result<Unit>
    suspend fun removeFavorite(hymnId: String): Result<Unit>
    
    /**
     * Sincroniza dados da nuvem para o local quando o usuário volta a ficar online.
     */
    suspend fun syncWhenOnline(): Result<Unit>
    
    /**
     * Verifica se há conflitos entre dados locais e remotos.
     */
    suspend fun checkForConflicts(): Boolean
    
    /**
     * Resolve conflitos entre dados locais e remotos.
     */
    suspend fun resolveConflicts(): Result<Unit>
}
