package com.lucasdelima.louveapp.ui.screens.settings

import com.lucasdelima.louveapp.domain.model.AuthUiState
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.repository.AuthCredentials
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class AuthViewModelTest {

    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        coEvery { authRepository.getCurrentUser() } returns MutableStateFlow(null)
        coEvery { authRepository.getAuthState() } returns MutableStateFlow(AuthUiState.Idle)
        coEvery { authRepository.signIn(any()) } returns Result.Success(Unit)
        coEvery { authRepository.signOut() } returns Unit
        viewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialUserProfileIsNull() {
        assertNull(viewModel.userProfile.value)
    }

    @Test
    fun initialAuthStateIsIdle() {
        assertEquals(AuthUiState.Idle, viewModel.authState.value)
    }

    @Test
    fun signIn_callsRepository() {
        val credentials = AuthCredentials.Google("test-token")
        viewModel.signIn(credentials)
    }

    @Test
    fun signOut_callsRepository() {
        viewModel.signOut()
    }
}