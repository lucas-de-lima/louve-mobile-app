package com.lucasdelima.louveapp.domain.repository

import com.lucasdelima.louveapp.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Representa os diferentes tipos de credenciais que podem ser usadas para fazer login.
 * Usar uma sealed interface nos dá segurança de tipos e torna o sistema facilmente extensível.
 */
sealed interface AuthCredentials {
    /** Credencial para login com a conta Google. */
    data object Google : AuthCredentials

    /** No futuro, podemos adicionar outras, como: */
    // data object Apple : AuthCredentials
    // data class Email(val email: String, val pass: String) : AuthCredentials
}

/**
 * A interface do repositório de autenticação.
 *
 * Define o contrato para todas as operações de autenticação no app.
 * A implementação concreta (usando Firebase, por exemplo) viverá na camada de 'data'.
 * Isso nos permite trocar o provedor de autenticação no futuro sem alterar a lógica de negócio.
 */
interface AuthRepository {

    /**
     * Um fluxo que emite o perfil do usuário atual.
     * Emite um objeto [UserProfile] se o usuário estiver logado, ou `null` se não estiver.
     * Permite que a UI reaja em tempo real a mudanças no estado de login/logout.
     */
    fun getCurrentUser(): Flow<UserProfile?>

    /**
     * Inicia um fluxo de login usando um tipo de credencial específico.
     * Este metodo é agnóstico do provedor. O ViewModel decide qual
     * tipo de credencial passar baseado na ação do usuário.
     *
     * @param credentials O tipo de credencial a ser usado (ex: AuthCredentials.Google).
     * @return Um [Result] que indica sucesso ou falha na operação de login.
     */
    suspend fun signIn(credentials: AuthCredentials): Result<Unit>

    /**
     * Realiza o logout do usuário atual.
     */
    suspend fun signOut()
}
