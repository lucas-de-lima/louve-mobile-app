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
}
