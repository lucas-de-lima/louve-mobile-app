package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.FavoritesRepository
import com.lucasdelima.louveapp.domain.repository.LocalFavoritesRepository
import com.lucasdelima.louveapp.domain.repository.UserRepository
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
        // flatMapLatest é a chave aqui. Ele observa o estado de login.
        // Se o usuário logar/deslogar, ele cancela a subscrição antiga e
        // cria uma nova para o repositório correto.
        return authRepository.getCurrentUser().flatMapLatest { user ->
            if (user != null) {
                // Usuário logado: usa o repositório remoto (UserRepository)
                remoteRepository.getFavoriteHymnIds()
            } else {
                // Usuário deslogado: usa o repositório local e mapeia para o tipo Result
                localRepository.getFavorites().map { favorites ->
                    Result.Success(favorites)
                }
            }
        }
    }

    override suspend fun addFavorite(hymnId: String): Result<Unit> {
        val user = authRepository.getCurrentUser().first()
        return if (user != null) {
            remoteRepository.addFavorite(hymnId)
        } else {
            localRepository.addFavorite(hymnId)
        }
    }

    override suspend fun removeFavorite(hymnId: String): Result<Unit> {
        val user = authRepository.getCurrentUser().first()
        return if (user != null) {
            remoteRepository.removeFavorite(hymnId)
        } else {
            localRepository.removeFavorite(hymnId)
        }
    }
}
