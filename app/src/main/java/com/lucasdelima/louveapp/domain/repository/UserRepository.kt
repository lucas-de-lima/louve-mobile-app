package com.lucasdelima.louveapp.domain.repository

import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.model.UserSettings
import com.lucasdelima.louveapp.domain.model.Result

import kotlinx.coroutines.flow.Flow

/**
 *
 * Contrato que define as operações de dados para um usuário logado.
 * Abstrai completamente a fonte de dados (Firestore) da lógica de negócio.
 */
interface UserRepository {

    /**
     * Garante que a estrutura inicial para um usuário (documentos de perfil, settings)
     * seja criada no Firestore. Deve ser chamado após um login bem-sucedido.
     * É uma operação "idempotente": se a estrutura já existe, não faz nada.
     *
     * @param userProfile O perfil do usuário obtido do provedor de autenticação.
     */
    suspend fun ensureUserStructure(userProfile: UserProfile): Result<Unit>

    /**
     * Observa as configurações do usuário em tempo real.
     * Emite um novo `UserSettings` sempre que os dados mudam no Firestore.
     * Se o usuário não tiver configurações salvas, deve emitir as configurações padrão.
     */
    fun getUserSettings(): Flow<Result<UserSettings>>

    /**
     * Atualiza as configurações do usuário no Firestore.
     *
     * @param settings O novo objeto de configurações a ser salvo.
     */
    suspend fun updateUserSettings(settings: UserSettings): Result<Unit>

    /**
     * Observa o conjunto de IDs dos hinos favoritos do usuário em tempo real.
     */
    fun getFavoriteHymnIds(): Flow<Result<Set<String>>>

    /**
     * Adiciona um hino à lista de favoritos do usuário de forma atômica.
     *
     * @param hymnId O ID numérico do hino a ser adicionado.
     */
    suspend fun addFavorite(hymnId: String): Result<Unit>

    /**
     * Remove um hino da lista de favoritos do usuário de forma atômica.
     *
     * @param hymnId O ID numérico do hino a ser removido.
     */
    suspend fun removeFavorite(hymnId: String): Result<Unit>
}
