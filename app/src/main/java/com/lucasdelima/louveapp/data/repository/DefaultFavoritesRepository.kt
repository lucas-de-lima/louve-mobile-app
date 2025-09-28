package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.FavoritesRepository
import com.lucasdelima.louveapp.domain.repository.LocalFavoritesRepository
import com.lucasdelima.louveapp.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 * Este é o "cérebro" da nossa nova arquitetura. Ele decide para onde delegar
 * as operações com base no estado de login do usuário.
 */

@Singleton
class DefaultFavoritesRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val localRepository: LocalFavoritesRepository,
    private val remoteRepository: UserRepository // Reutilizando o UserRepository para a parte remota
) : FavoritesRepository {

    override fun getFavoriteHymnIds(): Flow<Result<Set<String>>> {
        // SEMPRE usar localRepository que já tem os dados sincronizados
        // Se usuário logado: local tem dados da nuvem
        // Se usuário não logado: local tem dados locais
        return localRepository.getFavorites().map { favorites ->
            Result.Success(favorites)
        }
    }

    override suspend fun addFavorite(hymnId: String): Result<Unit> {
        // SEMPRE usar localRepository que já sincroniza com nuvem automaticamente
        return localRepository.addFavorite(hymnId)
    }

    override suspend fun removeFavorite(hymnId: String): Result<Unit> {
        // SEMPRE usar localRepository que já sincroniza com nuvem automaticamente
        return localRepository.removeFavorite(hymnId)
    }
    
    /**
     * Sincroniza dados da nuvem para o local quando o usuário volta a ficar online.
     * Este método deve ser chamado quando a conectividade é restaurada.
     */
    override suspend fun syncWhenOnline(): Result<Unit> {
        return try {
            val user = authRepository.getCurrentUser().first()
            if (user != null) {
                // Usuário está logado, sincronizar dados da nuvem para o local
                Result.Success(Unit) // Implementação simplificada por enquanto
            } else {
                Result.Success(Unit) // Usuário não logado, não há o que sincronizar
            }
        } catch (e: Exception) {
            Result.Error("Falha na sincronização: ${e.message}", e)
        }
    }
    
    /**
     * Verifica se há conflitos entre dados locais e remotos.
     */
    override suspend fun checkForConflicts(): Boolean {
        return try {
            val user = authRepository.getCurrentUser().first()
            if (user != null) {
                false // Implementação simplificada por enquanto
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Resolve conflitos entre dados locais e remotos.
     */
    override suspend fun resolveConflicts(): Result<Unit> {
        return try {
            val user = authRepository.getCurrentUser().first()
            if (user != null) {
                Result.Success(Unit) // Implementação simplificada por enquanto
            } else {
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error("Falha na resolução de conflitos: ${e.message}", e)
        }
    }
}
