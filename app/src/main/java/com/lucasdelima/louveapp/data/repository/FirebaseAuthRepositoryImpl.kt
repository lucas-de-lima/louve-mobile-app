package com.lucasdelima.louveapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.repository.AuthCredentials
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override fun getCurrentUser(): Flow<UserProfile?> = callbackFlow {
        // Cria um listener que observa mudanças no estado de autenticação do Firebase
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                // Se não há utilizador logado, emite null
                trySend(null).isSuccess
            } else {
                // Se há um utilizador, cria o nosso objeto UserProfile e emite-o
                val userProfile = UserProfile(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName,
                    email = firebaseUser.email,
                    photoUrl = firebaseUser.photoUrl?.toString()
                )
                trySend(userProfile).isSuccess
            }
        }

        // Adiciona o listener
        auth.addAuthStateListener(authStateListener)

        // Garante que o listener seja removido quando o Flow for cancelado
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun signIn(credentials: AuthCredentials): Result<Unit> {
        // A lógica específica para o Login com Google será implementada na camada de UI,
        // pois requer a interação do utilizador. O repositório lidará com o resultado.
        // Por enquanto, é um placeholder.
        return Result.failure(NotImplementedError("O método de login é iniciado na UI."))
    }

    override suspend fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            // Tratar exceções de logout se necessário
            e.printStackTrace()
        }
    }
}
