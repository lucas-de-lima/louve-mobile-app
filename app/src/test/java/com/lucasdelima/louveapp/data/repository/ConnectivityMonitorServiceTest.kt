package com.lucasdelima.louveapp.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.FavoritesRepository
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertTrue

class ConnectivityMonitorServiceTest {

    private val context: Context = mockk(relaxed = true)
    private val connectivityManager: ConnectivityManager = mockk(relaxUnitFun = true)
    private val authRepository: AuthRepository = mockk()
    private val favoritesRepository: FavoritesRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private lateinit var service: ConnectivityMonitorService

    @Before
    fun setup() {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { authRepository.getCurrentUser() } returns MutableStateFlow(null)
        service = ConnectivityMonitorService(context, authRepository, favoritesRepository, settingsRepository)
    }

    @After
    fun tearDown() {
        service.cleanup()
    }

    @Test
    fun startMonitoring_doesNotThrow() {
        service.startMonitoring()
    }

    @Test
    fun startMonitoring_isIdempotent() {
        service.startMonitoring()
        service.startMonitoring()
    }

    @Test
    fun stopMonitoring_doesNotThrow() {
        service.stopMonitoring()
    }

    @Test
    fun stopMonitoring_afterStart_doesNotThrow() {
        service.startMonitoring()
        service.stopMonitoring()
    }

    @Test
    fun cleanup_stopsMonitoring() {
        service.startMonitoring()
        service.cleanup()
    }

    @Test
    fun forceSync_withLoggedUserAndNetworkAvailable_callsSync() = runTest {
        val user = UserProfile("uid", "User", "user@test.com", null)
        val network: Network = mockk()
        val capabilities: NetworkCapabilities = mockk()
        every { authRepository.getCurrentUser() } returns MutableStateFlow(user)
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true
        coEvery { favoritesRepository.syncWhenOnline() } returns Result.Success(Unit)
        coEvery { settingsRepository.syncWhenOnline() } returns Result.Success(Unit)
        coEvery { favoritesRepository.checkForConflicts() } returns false

        service.forceSync()

        coVerify(timeout = 3000) { favoritesRepository.syncWhenOnline() }
        coVerify(timeout = 3000) { settingsRepository.syncWhenOnline() }
    }

    @Test
    fun forceSync_withoutLoggedUser_skipsSync() = runTest {
        every { authRepository.getCurrentUser() } returns MutableStateFlow(null)

        service.forceSync()

        coVerify(exactly = 0) { favoritesRepository.syncWhenOnline() }
    }

    @Test
    fun forceSync_resolvesConflictsWhenDetected() = runTest {
        val user = UserProfile("uid", "User", "user@test.com", null)
        val network: Network = mockk()
        val capabilities: NetworkCapabilities = mockk()
        every { authRepository.getCurrentUser() } returns MutableStateFlow(user)
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true
        coEvery { favoritesRepository.syncWhenOnline() } returns Result.Success(Unit)
        coEvery { settingsRepository.syncWhenOnline() } returns Result.Success(Unit)
        coEvery { favoritesRepository.checkForConflicts() } returns true
        coEvery { favoritesRepository.resolveConflicts() } returns Result.Success(Unit)

        service.forceSync()

        coVerify(timeout = 3000) { favoritesRepository.resolveConflicts() }
    }
}