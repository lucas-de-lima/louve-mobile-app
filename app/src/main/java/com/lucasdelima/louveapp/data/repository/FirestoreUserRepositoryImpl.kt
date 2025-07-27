package com.lucasdelima.louveapp.data.repository

/**
 * file: data/src/main/java/com/louveapp/data/repository/FirestoreUserRepositoryImpl.kt
 *
 * Implementação concreta do UserRepository que utiliza o Cloud Firestore como backend.
 * Esta classe lida com toda a complexidade de acesso, serialização e escuta de
 * eventos do Firestore.
 */
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.model.UserSettings
import com.lucasdelima.louveapp.domain.repository.UserRepository
import com.lucasdelima.louveapp.domain.model.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreUserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    // Constantes para evitar "magic strings" e facilitar a manutenção.
    private object FirestorePaths {
        const val USERS_COLLECTION = "users"
        const val SETTINGS_DOCUMENT = "settings"
        const val FAVORITES_COLLECTION = "favorites"
        const val FAVORITES_DOCUMENT = "hymns"
        const val FAVORITE_IDS_FIELD = "ids"
    }

    // Propriedade computada para obter o UID do usuário atual de forma segura.
    private val currentUserId: String?
        get() = auth.currentUser?.uid

    override suspend fun ensureUserStructure(userProfile: UserProfile): Result<Unit> {
        val userId = userProfile.uid
        return try {
            // Usamos uma transação para garantir a atomicidade da operação.
            // Se o usuário já existe, não faremos nada. Se não, criamos a estrutura.
            firestore.runTransaction { transaction ->
                val userDocRef = firestore.collection(FirestorePaths.USERS_COLLECTION).document(userId)
                val userSnapshot = transaction.get(userDocRef)

                // Só criamos a estrutura se o documento principal do usuário não existir.
                if (!userSnapshot.exists()) {
                    // 1. Salva os dados do perfil (nome, email, etc.)
                    transaction.set(userDocRef, userProfile)

                    // 2. Cria o documento de configurações com valores padrão.
                    val settingsDocRef = userDocRef.collection(FirestorePaths.USERS_COLLECTION)
                        .document(FirestorePaths.SETTINGS_DOCUMENT)
                    transaction.set(settingsDocRef, UserSettings()) // Usa o construtor padrão

                    // 3. Cria o documento de favoritos com uma lista vazia.
                    val favoritesDocRef = userDocRef.collection(FirestorePaths.FAVORITES_COLLECTION)
                        .document(FirestorePaths.FAVORITES_DOCUMENT)
                    transaction.set(favoritesDocRef, mapOf(FirestorePaths.FAVORITE_IDS_FIELD to emptyList<String>()))
                }
                // A transação retorna null se for bem-sucedida
                null
            }.await()
            Result.Success(Unit)
        } catch (e: Exception) {
            // Log.e("FirestoreUserRepository", "Error ensuring user structure", e)
            Result.Error("Falha ao inicializar dados do usuário.", e)
        }
    }

    override fun getUserSettings(): Flow<Result<UserSettings>> = callbackFlow {
        val userId = currentUserId
        if (userId == null) {
            trySend(Result.Error("Usuário não autenticado."))
            close()
            return@callbackFlow
        }

        val settingsDocRef = firestore.collection(FirestorePaths.USERS_COLLECTION).document(userId)
            .collection(FirestorePaths.USERS_COLLECTION).document(FirestorePaths.SETTINGS_DOCUMENT)

        val listener = settingsDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.Error("Erro ao observar configurações.", error))
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val settings = snapshot.toObject(UserSettings::class.java) ?: UserSettings()
                trySend(Result.Success(settings))
            } else {
                // Se o documento não existe, envia as configurações padrão.
                trySend(Result.Success(UserSettings()))
            }
        }
        // Garante que o listener seja removido quando o Flow for cancelado.
        awaitClose { listener.remove() }
    }

    override suspend fun updateUserSettings(settings: UserSettings): Result<Unit> {
        val userId = currentUserId ?: return Result.Error("Usuário não autenticado.")
        return try {
            val settingsDocRef = firestore.collection(FirestorePaths.USERS_COLLECTION).document(userId)
                .collection(FirestorePaths.USERS_COLLECTION).document(FirestorePaths.SETTINGS_DOCUMENT)
            // SetOptions.merge() é importante para não sobrescrever outros campos no futuro.
            settingsDocRef.set(settings, SetOptions.merge()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Falha ao atualizar o tema.", e)
        }
    }

    override fun getFavoriteHymnIds(): Flow<Result<Set<String>>> = callbackFlow {
        val userId = currentUserId
        if (userId == null) {
            trySend(Result.Error("Usuário não autenticado."))
            close()
            return@callbackFlow
        }

        val favoritesDocRef = firestore.collection(FirestorePaths.USERS_COLLECTION).document(userId)
            .collection(FirestorePaths.FAVORITES_COLLECTION).document(FirestorePaths.FAVORITES_DOCUMENT)

        val listener = favoritesDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.Error("Erro ao observar favoritos.", error))
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                @Suppress("UNCHECKED_CAST")
                val ids = snapshot.get(FirestorePaths.FAVORITE_IDS_FIELD) as? List<String>
                trySend(Result.Success(ids?.toSet() ?: emptySet()))
            } else {
                // Se o documento não existe, a lista de favoritos está vazia.
                trySend(Result.Success(emptySet()))
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addFavorite(hymnId: String): Result<Unit> {
        val userId = currentUserId ?: return Result.Error("Usuário não autenticado.")
        return try {
            val favoritesDocRef = firestore.collection(FirestorePaths.USERS_COLLECTION).document(userId)
                .collection(FirestorePaths.FAVORITES_COLLECTION).document(FirestorePaths.FAVORITES_DOCUMENT)

            // FieldValue.arrayUnion é atômico e idempotente.
            favoritesDocRef.update(FirestorePaths.FAVORITE_IDS_FIELD, FieldValue.arrayUnion(hymnId)).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Falha ao favoritar o hino.", e)
        }
    }

    override suspend fun removeFavorite(hymnId: String): Result<Unit> {
        val userId = currentUserId ?: return Result.Error("Usuário não autenticado.")
        return try {
            val favoritesDocRef = firestore.collection(FirestorePaths.USERS_COLLECTION).document(userId)
                .collection(FirestorePaths.FAVORITES_COLLECTION).document(FirestorePaths.FAVORITES_DOCUMENT)

            // FieldValue.arrayRemove é atômico.
            favoritesDocRef.update(FirestorePaths.FAVORITE_IDS_FIELD, FieldValue.arrayRemove(hymnId)).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Falha ao desfavoritar o hino.", e)
        }
    }
}
