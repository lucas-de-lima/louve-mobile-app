package com.lucasdelima.louveapp.domain.repository

import com.lucasdelima.louveapp.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Representa os diferentes tipos de credenciais que podem ser usadas para fazer login.
 * Esta versão é focada na estabilidade, usando um contrato direto para cada provedor.
 */
sealed interface AuthCredentials {
    /**
     * Credencial para login com a conta Google.
     * @param idToken O token de ID fornecido pelo SDK estável do Google Sign-In.
     */
    data class Google(val idToken: String) : AuthCredentials
}

/**
 * A interface do repositório de autenticação.
 *
 * Define o contrato para todas as operações de autenticação no app, de forma
 * agnóstica ao provedor (Firebase, etc.), garantindo que a lógica de negócio
 * não dependa de implementações específicas.
 */
interface AuthRepository {

    /**
     * Um fluxo que emite o perfil do usuário atual.
     * Emite um objeto [UserProfile] se o usuário estiver logado, ou `null` se não estiver.
     */
    fun getCurrentUser(): Flow<UserProfile?>

    /**
     * Tenta realizar o login no Firebase usando uma credencial específica.
     *
     * @param credentials A credencial a ser usada (ex: AuthCredentials.Google).
     * @return Um [Result] que indica sucesso ou falha na operação de login.
     */
    suspend fun signIn(credentials: AuthCredentials): Result<Unit>

    /**
     * Realiza o logout do usuário atual.
     */
    suspend fun signOut()
}
