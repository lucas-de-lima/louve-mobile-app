package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.model.ThemeDefaults
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.model.UserSettings
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class DefaultSettingsRepositoryTest {

    private val authRepository: AuthRepository = mockk()
    private val localSettingsRepository: LocalSettingsRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val authStateFlow = MutableStateFlow<UserProfile?>(null)
    private lateinit var repository: DefaultSettingsRepository

    @Before
    fun setup() {
        every { authRepository.getCurrentUser() } returns authStateFlow
        repository = DefaultSettingsRepository(
            authRepository,
            localSettingsRepository,
            userRepository
        )
    }

    @Test
    fun theme_whenUserNotLogged_returnsLocalTheme() = runTest {
        authStateFlow.value = null
        every { localSettingsRepository.theme } returns MutableStateFlow("dark_theme")

        val theme = repository.theme.first()

        assertEquals("dark_theme", theme)
    }

    @Test
    fun theme_whenUserLogged_returnsRemoteTheme() = runTest {
        authStateFlow.value = UserProfile("uid", "User", "user@test.com", null)
        every { userRepository.getUserSettings() } returns MutableStateFlow(Result.Success(UserSettings("dark_theme")))

        val theme = repository.theme.first()

        assertEquals("dark_theme", theme)
    }

    @Test
    fun theme_whenRemoteFails_returnsDefaultTheme() = runTest {
        authStateFlow.value = UserProfile("uid", "User", "user@test.com", null)
        every { userRepository.getUserSettings() } returns MutableStateFlow(Result.Error("Not found"))

        val theme = repository.theme.first()

        assertEquals(ThemeDefaults.THEME_ID, theme)
    }

    @Test
    fun saveTheme_whenUserNotLogged_savesLocally() = runTest {
        authStateFlow.value = null
        coEvery { localSettingsRepository.saveTheme(any()) } returns Unit

        repository.saveTheme("dark_theme")

        coVerify(exactly = 1) { localSettingsRepository.saveTheme("dark_theme") }
        coVerify(exactly = 0) { userRepository.updateUserSettings(any()) }
    }

    @Test
    fun saveTheme_whenUserLogged_savesRemotely() = runTest {
        authStateFlow.value = UserProfile("uid", "User", "user@test.com", null)
        coEvery { userRepository.updateUserSettings(any()) } returns Result.Success(Unit)

        repository.saveTheme("dark_theme")

        coVerify(exactly = 1) { userRepository.updateUserSettings(UserSettings("dark_theme")) }
        coVerify(exactly = 0) { localSettingsRepository.saveTheme(any()) }
    }

    @Test
    fun fontScaleFactor_delegatesToLocal() = runTest {
        every { localSettingsRepository.fontScaleFactor } returns MutableStateFlow(1.5f)

        val factor = repository.fontScaleFactor.first()

        assertEquals(1.5f, factor)
    }

    @Test
    fun saveFontScaleFactor_delegatesToLocal() = runTest {
        coEvery { localSettingsRepository.saveFontScaleFactor(any()) } returns Unit

        repository.saveFontScaleFactor(0.8f)

        coVerify(exactly = 1) { localSettingsRepository.saveFontScaleFactor(0.8f) }
    }

    @Test
    fun syncWhenOnline_withoutUser_returnsSuccess() = runTest {
        authStateFlow.value = null

        val result = repository.syncWhenOnline()

        assertTrue(result is Result.Success)
    }

    @Test
    fun syncWhenOnline_withLoggedUser_returnsSuccess() = runTest {
        authStateFlow.value = UserProfile("uid", "User", "user@test.com", null)

        val result = repository.syncWhenOnline()

        assertTrue(result is Result.Success)
    }

    @Test
    fun syncWhenOnline_whenException_returnsError() = runTest {
        every { authRepository.getCurrentUser() }.throws(RuntimeException("Network error"))

        val result = repository.syncWhenOnline()

        assertTrue(result is Result.Error)
    }
}