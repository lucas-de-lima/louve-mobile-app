package com.lucasdelima.louveapp.domain.repository

/**
 *
 * Define o contrato para gerenciar favoritos salvos localmente no dispositivo.
 */

import com.lucasdelima.louveapp.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface LocalFavoritesRepository {
    fun getFavorites(): Flow<Set<String>>
    suspend fun addFavorite(hymnId: String): Result<Unit>
    suspend fun removeFavorite(hymnId: String): Result<Unit>
    suspend fun clearFavorites(): Result<Unit>
    
    /**
     * Sincroniza favoritos locais com uma nova lista.
     * Substitui todos os favoritos existentes pelos novos.
     */
    suspend fun syncFavorites(favorites: Set<String>): Result<Unit>
}
