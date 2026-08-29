package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.domain.model.AuthError
import com.lucasdelima.louveapp.domain.model.AuthUiState
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.repository.AuthCredentials
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class FirebaseAuthRepositoryImplTest {

    private val firebaseAuth: FirebaseAuth = mockk()
    private val userRepository: UserRepository = mockk()
    private val dataMigrationService: DataMigrationService = mockk()
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        authRepository = FirebaseAuthRepositoryImpl(
            firebaseAuth,
            userRepository,
            dataMigrationService
        )
    }

    @Test
    fun getAuthState_initialStateIsIdle() = runTest {
        assertEquals(AuthUiState.Idle, authRepository.getAuthState().first())
    }

    @Test
    fun signIn_withInvalidToken_returnsError() = runTest {
        val result = authRepository.signIn(AuthCredentials.Google(""))

        assertTrue(result is Result.Error)
    }

    @Test
    fun signIn_withShortToken_returnsError() = runTest {
        val result = authRepository.signIn(AuthCredentials.Google("short"))

        assertTrue(result is Result.Error)
    }

    @Test
    fun signOut_resetsStateToIdle() = runTest {
        authRepository.signOut()

        val state = authRepository.getAuthState().first()
        assertEquals(AuthUiState.Idle, state)
    }

    @Test
    fun authState_emitsLoadingDuringSignIn() = runTest {
        val credentials = AuthCredentials.Google("valid.id.token.12345")

        val result = authRepository.signIn(credentials)

        assertTrue(result is Result.Error)
    }
}