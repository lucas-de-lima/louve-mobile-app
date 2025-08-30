package com.lucasdelima.louveapp.data.repository

/**
 * file: data/src/main/java/com/louveapp/data/repository/FirestoreUserRepositoryImpl.kt
 *
 * Implementação concreta do UserRepository que utiliza o Cloud Firestore como backend.
 * Esta classe lida com toda a complexidade de acesso, serialização e escuta de
 * eventos do Firestore.
 */
import android.util.Log
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        Log.d("FirestoreUserRepository", "🔍 ensureUserStructure iniciado para usuário: $userId")
        
        return try {
            // Usamos uma transação para garantir a atomicidade da operação.
            // Agora verificamos se as subcoleções existem, não apenas o documento principal.
            val structureResult = firestore.runTransaction { transaction ->
                val userDocRef = firestore.collection(FirestorePaths.USERS_COLLECTION).document(userId)
                val userSnapshot = transaction.get(userDocRef)

                Log.d("FirestoreUserRepository", "📄 Documento principal existe: ${userSnapshot.exists()}")

                // 1. Se o documento principal não existir, criamos tudo
                if (!userSnapshot.exists()) {
                    Log.d("FirestoreUserRepository", "🆕 Usuário novo - criando estrutura completa")
                    // Salva os dados do perfil (nome, email, etc.)
                    transaction.set(userDocRef, userProfile)
                    
                    // Cria as subcoleções
                    createSubcollections(transaction, userDocRef)
                } else {
                    Log.d("FirestoreUserRepository", "👤 Usuário existente - verificando subcoleções")
                    // 2. Se o documento existe, verificamos se as subcoleções existem
                    val settingsDocRef = userDocRef.collection(FirestorePaths.USERS_COLLECTION)
                        .document(FirestorePaths.SETTINGS_DOCUMENT)
                    val favoritesDocRef = userDocRef.collection(FirestorePaths.FAVORITES_COLLECTION)
                        .document(FirestorePaths.FAVORITES_DOCUMENT)
                    
                    // Verifica se as subcoleções existem
                    val settingsExists = transaction.get(settingsDocRef).exists()
                    val favoritesExists = transaction.get(favoritesDocRef).exists()
                    
                    Log.d("FirestoreUserRepository", "🔍 Subcoleções: settings=${settingsExists}, favorites=${favoritesExists}")
                    
                    // Cria subcoleções faltantes
                    if (!settingsExists || !favoritesExists) {
                        Log.d("FirestoreUserRepository", "⚠️ Subcoleções faltantes - criando...")
                        createSubcollections(transaction, userDocRef)
                    } else {
                        Log.d("FirestoreUserRepository", "✅ Todas as subcoleções já existem")
                    }
                }
                // A transação retorna null se for bem-sucedida
                null
            }.await()
            
            // Após criar/verificar a estrutura, a migração será executada pelo sistema de sincronização
            if (structureResult == null) {
                Log.d("FirestoreUserRepository", "🎉 Estrutura do usuário verificada/criada com sucesso")
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreUserRepository", "❌ Error ensuring user structure", e)
            Result.Error("Falha ao inicializar dados do usuário: ${e.message}", e)
        }
    }
    
    /**
     * Cria as subcoleções necessárias para o usuário.
     * Esta função é chamada tanto para usuários novos quanto existentes.
     */
    private fun createSubcollections(
        transaction: com.google.firebase.firestore.Transaction,
        userDocRef: com.google.firebase.firestore.DocumentReference
    ) {
        Log.d("FirestoreUserRepository", "🏗️ Criando subcoleções para usuário")
        
        // 1. Cria o documento de configurações com valores padrão
        val settingsDocRef = userDocRef.collection(FirestorePaths.USERS_COLLECTION)
            .document(FirestorePaths.SETTINGS_DOCUMENT)
        transaction.set(settingsDocRef, UserSettings())
        Log.d("FirestoreUserRepository", "✅ Documento settings criado")
        
        // 2. Cria o documento de favoritos com uma lista vazia
        val favoritesDocRef = userDocRef.collection(FirestorePaths.FAVORITES_COLLECTION)
            .document(FirestorePaths.FAVORITES_DOCUMENT)
        transaction.set(favoritesDocRef, mapOf(FirestorePaths.FAVORITE_IDS_FIELD to emptyList<String>()))
        Log.d("FirestoreUserRepository", "✅ Documento favorites criado")
        
        Log.d("FirestoreUserRepository", "🎉 Todas as subcoleções criadas com sucesso")
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
        Log.d("FirestoreUserRepository", "🔧 updateUserSettings iniciado para usuário: $userId")
        
        return try {
            val settingsDocRef = firestore.collection(FirestorePaths.USERS_COLLECTION).document(userId)
                .collection(FirestorePaths.USERS_COLLECTION).document(FirestorePaths.SETTINGS_DOCUMENT)
            
            Log.d("FirestoreUserRepository", "📝 Salvando configurações: $settings")
            
            // SetOptions.merge() é importante para não sobrescrever outros campos no futuro.
            settingsDocRef.set(settings, SetOptions.merge()).await()
            
            Log.d("FirestoreUserRepository", "✅ Configurações salvas com sucesso")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreUserRepository", "❌ Falha ao atualizar configurações", e)
            Result.Error("Falha ao atualizar o tema: ${e.message}", e)
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
        Log.d("FirestoreUserRepository", "⭐ addFavorite iniciado para usuário: $userId, hino: $hymnId")
        
        return try {
            val favoritesDocRef = firestore.collection(FirestorePaths.USERS_COLLECTION).document(userId)
                .collection(FirestorePaths.FAVORITES_COLLECTION).document(FirestorePaths.FAVORITES_DOCUMENT)

            Log.d("FirestoreUserRepository", "📝 Adicionando favorito: $hymnId")
            
            // Primeiro, obtém a lista atual de favoritos
            val currentFavorites = try {
                val snapshot = favoritesDocRef.get().await()
                if (snapshot.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    snapshot.get(FirestorePaths.FAVORITE_IDS_FIELD) as? List<String> ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.w("FirestoreUserRepository", "⚠️ Erro ao ler favoritos existentes, assumindo lista vazia", e)
                emptyList()
            }
            
            // Adiciona o novo favorito à lista
            val updatedFavorites = (currentFavorites + hymnId).distinct()
            
            // Salva a lista atualizada usando set com merge
            favoritesDocRef.set(
                mapOf(FirestorePaths.FAVORITE_IDS_FIELD to updatedFavorites),
                SetOptions.merge()
            ).await()
            
            Log.d("FirestoreUserRepository", "✅ Favorito adicionado com sucesso. Total: ${updatedFavorites.size}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreUserRepository", "❌ Falha ao favoritar hino: $hymnId", e)
            Result.Error("Falha ao favoritar o hino: ${e.message}", e)
        }
    }

    override suspend fun removeFavorite(hymnId: String): Result<Unit> {
        val userId = currentUserId ?: return Result.Error("Usuário não autenticado.")
        Log.d("FirestoreUserRepository", "🗑️ removeFavorite iniciado para usuário: $userId, hino: $hymnId")
        
        return try {
            val favoritesDocRef = firestore.collection(FirestorePaths.USERS_COLLECTION).document(userId)
                .collection(FirestorePaths.FAVORITES_COLLECTION).document(FirestorePaths.FAVORITES_DOCUMENT)

            Log.d("FirestoreUserRepository", "📝 Removendo favorito: $hymnId")
            
            // Primeiro, obtém a lista atual de favoritos
            val currentFavorites = try {
                val snapshot = favoritesDocRef.get().await()
                if (snapshot.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    snapshot.get(FirestorePaths.FAVORITE_IDS_FIELD) as? List<String> ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.w("FirestoreUserRepository", "⚠️ Erro ao ler favoritos existentes, assumindo lista vazia", e)
                emptyList()
            }
            
            // Remove o favorito da lista
            val updatedFavorites = currentFavorites.filter { it != hymnId }
            
            // Salva a lista atualizada usando set com merge
            favoritesDocRef.set(
                mapOf(FirestorePaths.FAVORITE_IDS_FIELD to updatedFavorites),
                SetOptions.merge()
            ).await()
            
            Log.d("FirestoreUserRepository", "✅ Favorito removido com sucesso. Total: ${updatedFavorites.size}")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreUserRepository", "❌ Falha ao remover favorito: $hymnId", e)
            Result.Error("Falha ao desfavoritar o hino: ${e.message}", e)
        }
    }


}