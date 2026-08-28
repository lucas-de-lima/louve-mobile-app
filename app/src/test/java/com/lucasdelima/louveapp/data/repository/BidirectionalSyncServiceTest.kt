package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.model.ThemeDefaults
import com.lucasdelima.louveapp.domain.repository.LocalFavoritesRepository
import com.lucasdelima.louveapp.domain.repository.LocalSettingsRepository
import com.lucasdelima.louveapp.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertTrue

class BidirectionalSyncServiceTest {

    private val localFavoritesRepository: LocalFavoritesRepository = mockk()
    private val localSettingsRepository: LocalSettingsRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private lateinit var service: BidirectionalSyncService

    @Before
    fun setup() {
        service = BidirectionalSyncService(
            localFavoritesRepository,
            localSettingsRepository,
            userRepository
        )
    }

    @Test
    fun syncRemoteToLocal_addsMissingRemoteFavorites() = runTest {
        coEvery { userRepository.getFavoriteHymnIds() } returns MutableStateFlow(Result.Success(setOf("1", "2", "3")))
        coEvery { userRepository.getUserSettings() } returns MutableStateFlow(Result.Success(com.lucasdelima.louveapp.domain.model.UserSettings(ThemeDefaults.THEME_ID)))
        coEvery { localFavoritesRepository.getFavorites() } returns MutableStateFlow(setOf("1"))
        coEvery { localFavoritesRepository.addFavorite(any()) } returns Result.Success(Unit)
        coEvery { localFavoritesRepository.removeFavorite(any()) } returns Result.Success(Unit)
        coEvery { localSettingsRepository.theme } returns MutableStateFlow(ThemeDefaults.THEME_ID)
        coEvery { localSettingsRepository.saveTheme(any()) } returns Unit

        val result = service.syncRemoteToLocal()

        assertTrue(result is Result.Success)
        coVerify { localFavoritesRepository.addFavorite("2") }
        coVerify { localFavoritesRepository.addFavorite("3") }
        coVerify(exactly = 0) { localFavoritesRepository.removeFavorite(any()) }
    }

    @Test
    fun syncRemoteToLocal_removesLocalFavoritesNotInRemote() = runTest {
        coEvery { userRepository.getFavoriteHymnIds() } returns MutableStateFlow(Result.Success(setOf("1")))
        coEvery { userRepository.getUserSettings() } returns MutableStateFlow(Result.Success(com.lucasdelima.louveapp.domain.model.UserSettings(ThemeDefaults.THEME_ID)))
        coEvery { localFavoritesRepository.getFavorites() } returns MutableStateFlow(setOf("1", "2", "3"))
        coEvery { localFavoritesRepository.addFavorite(any()) } returns Result.Success(Unit)
        coEvery { localFavoritesRepository.removeFavorite(any()) } returns Result.Success(Unit)
        coEvery { localSettingsRepository.theme } returns MutableStateFlow(ThemeDefaults.THEME_ID)
        coEvery { localSettingsRepository.saveTheme(any()) } returns Unit

        val result = service.syncRemoteToLocal()

        assertTrue(result is Result.Success)
        coVerify { localFavoritesRepository.removeFavorite("2") }
        coVerify { localFavoritesRepository.removeFavorite("3") }
    }

    @Test
    fun syncRemoteToLocal_updatesThemeIfDifferent() = runTest {
        coEvery { userRepository.getFavoriteHymnIds() } returns MutableStateFlow(Result.Success(emptySet()))
        coEvery { userRepository.getUserSettings() } returns MutableStateFlow(Result.Success(com.lucasdelima.louveapp.domain.model.UserSettings("dark_theme")))
        coEvery { localFavoritesRepository.getFavorites() } returns MutableStateFlow(emptySet())
        coEvery { localSettingsRepository.theme } returns MutableStateFlow(ThemeDefaults.THEME_ID)
        coEvery { localSettingsRepository.saveTheme(any()) } returns Unit
        coEvery { localFavoritesRepository.addFavorite(any()) } returns Result.Success(Unit)
        coEvery { localFavoritesRepository.removeFavorite(any()) } returns Result.Success(Unit)

        val result = service.syncRemoteToLocal()

        assertTrue(result is Result.Success)
        coVerify { localSettingsRepository.saveTheme("dark_theme") }
    }

    @Test
    fun hasConflicts_detectsDifferences() = runTest {
        coEvery { userRepository.getFavoriteHymnIds() } returns MutableStateFlow(Result.Success(setOf("1", "2", "3")))
        coEvery { localFavoritesRepository.getFavorites() } returns MutableStateFlow(setOf("1"))

        val hasConflict = service.hasConflicts()

        assertTrue(hasConflict)
    }
}