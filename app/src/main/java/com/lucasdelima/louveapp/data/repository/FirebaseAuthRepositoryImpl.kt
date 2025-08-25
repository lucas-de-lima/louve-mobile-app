package com.lucasdelima.louveapp.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.AuthCredentials
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.ui.screens.settings.AuthUiState
import com.lucasdelima.louveapp.ui.screens.settings.AuthError
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlinx.coroutines.launch

private const val TAG = "FirebaseAuthRepository"

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    /**
     * Estado interno da autenticação.
     * Controla o que é emitido para a UI baseado no estado atual.
     */
    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    
    override fun getAuthState(): Flow<AuthUiState> = _authState.asStateFlow()

    override fun getCurrentUser(): Flow<UserProfile?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                trySend(null)
                _authState.value = AuthUiState.Idle
            } else {
                val userProfile = UserProfile(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName,
                    email = firebaseUser.email,
                    photoUrl = firebaseUser.photoUrl?.toString()
                )
                trySend(userProfile)
                _authState.value = AuthUiState.Success(userProfile)
            }
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    override suspend fun signIn(credentials: AuthCredentials): Result<Unit> {
        return try {
            _authState.value = AuthUiState.Loading
            
            when (credentials) {
                is AuthCredentials.Google -> {
                    // Validar token antes de enviar para Firebase
                    if (!isValidGoogleToken(credentials.idToken)) {
                        _authState.value = AuthUiState.Error(
                            AuthError.InvalidCredentials,
                            { 
                                // Usar coroutine scope para chamar função suspend
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                    signIn(credentials)
                                }
                            }
                        )
                        return Result.Error("Token Google inválido")
                    }

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

                    // Estado de sucesso será definido pelo AuthStateListener
                    Result.Success(Unit)
                }
            }
        } catch (e: Exception) {
            val authError = when (e) {
                is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidCredentials
                is FirebaseAuthInvalidUserException -> AuthError.InvalidCredentials
                else -> {
                    // Verificar se é um erro de rede baseado na mensagem
                    if (e.message?.contains("network", ignoreCase = true) == true) {
                        AuthError.NetworkError
                    } else {
                        AuthError.UnknownError(e.message ?: "Erro desconhecido")
                    }
                }
            }
            
            _authState.value = AuthUiState.Error(authError) { 
                // Usar coroutine scope para chamar função suspend
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    signIn(credentials)
                }
            }
            Log.e(TAG, "Falha no signIn: ", e)
            Result.Error(e.message ?: "Falha na autenticação", e)
        }
    }

    /**
     * Valida se o token Google é válido antes de enviar para Firebase.
     * Verifica se o token não está vazio e tem tamanho mínimo.
     * 
     * @param idToken O token ID do Google
     * @return true se o token parece válido, false caso contrário
     */
    private fun isValidGoogleToken(idToken: String): Boolean {
        return idToken.isNotBlank() && idToken.length > 20
    }

    override suspend fun signOut() {
        try {
            auth.signOut()
            _authState.value = AuthUiState.Idle
        } catch (e: Exception) {
            Log.e(TAG, "Falha no signOut: ", e)
        }
    }
}
