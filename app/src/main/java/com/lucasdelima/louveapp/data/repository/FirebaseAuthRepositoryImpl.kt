package com.lucasdelima.louveapp.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.AuthCredentials
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.UserRepository
import com.lucasdelima.louveapp.domain.model.AuthUiState
import com.lucasdelima.louveapp.domain.model.AuthError
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlinx.coroutines.launch

private const val TAG = "FirebaseAuthRepository"

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val syncScheduler: SyncScheduler,
    private val dataMigrationService: DataMigrationService
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
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    createdAt = System.currentTimeMillis()
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

                    withContext(NonCancellable + kotlinx.coroutines.Dispatchers.IO) {
                        val firebaseUser = authResult.user
                        if (firebaseUser != null) {
                            val userProfile = UserProfile(
                                uid = firebaseUser.uid,
                                name = firebaseUser.displayName,
                                email = firebaseUser.email,
                                photoUrl = firebaseUser.photoUrl?.toString(),
                                createdAt = System.currentTimeMillis()
                            )

                            Log.d(TAG, "🔧 Garantindo estrutura do usuário após login")
                            val structureResult = userRepository.ensureUserStructure(userProfile)
                            if (structureResult is Result.Error) {
                                Log.w(TAG, "⚠️ Falha ao criar estrutura do usuário: ${structureResult.message}")
                            } else {
                                val migrationResult = dataMigrationService.migrateLocalDataToCloud()
                                if (migrationResult is Result.Error) {
                                    Log.w(TAG, "Falha na migração de dados: ${migrationResult.message}")
                                }
                                Log.d(TAG, "✅ Estrutura do usuário verificada. Diferindo sync para WorkManager.")
                                syncScheduler.scheduleSync()
                            }
                        }
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

    override suspend fun deactivateAccount(): Result<Unit> {
        return try {
            auth.currentUser?.delete()
            auth.signOut()
            _authState.value = AuthUiState.Idle
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao desativar conta: ", e)
            Result.Error("Falha ao desativar conta", e)
        }
    }
}
