package com.lucasdelima.louveapp.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.repository.AuthCredentials
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "FirebaseAuthRepository"

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun getCurrentUser(): Flow<UserProfile?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                val userProfile = UserProfile(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName,
                    email = firebaseUser.email,
                    photoUrl = firebaseUser.photoUrl?.toString()
                )
                trySend(userProfile)
            }
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    override suspend fun signIn(credentials: AuthCredentials): Result<Unit> {
        return try {
            when (credentials) {
                is AuthCredentials.Google -> {
                    val firebaseCredential = GoogleAuthProvider.getCredential(credentials.idToken, null)
                    val authResult = auth.signInWithCredential(firebaseCredential).await()

                    // --- LÓGICA DE ROBUSTEZ ADICIONADA ---
                    // Após o login bem-sucedido, garantimos que um perfil de usuário
                    // existe no nosso banco de dados Firestore.
                    val user = authResult.user
                    if (user != null) {
                        val userProfileData = mapOf(
                            "uid" to user.uid,
                            "name" to user.displayName,
                            "email" to user.email,
                            "photoUrl" to user.photoUrl?.toString(),
                            "createdAt" to System.currentTimeMillis()
                        )
                        // Usamos .set() com SetOptions.merge() para criar o documento se ele
                        // não existir, ou atualizar os dados se ele já existir, sem
                        // sobrescrever outros campos (como a lista de favoritos no futuro).
                        firestore.collection("users").document(user.uid)
                            .set(userProfileData, SetOptions.merge())
                            .await()
                    }

                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Falha no signIn: ", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            Log.e(TAG, "Falha no signOut: ", e)
        }
    }
}
