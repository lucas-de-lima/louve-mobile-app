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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class DataMigrationServiceTest {

    private val localFavoritesRepository: LocalFavoritesRepository = mockk()
    private val localSettingsRepository: LocalSettingsRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private lateinit var service: DataMigrationService

    @Before
    fun setup() {
        service = DataMigrationService(
            localFavoritesRepository,
            localSettingsRepository,
            userRepository
        )
    }

    @Test
    fun migrateLocalDataToCloud_withLocalFavorites_migratesToCloud() = runTest {
        coEvery { localFavoritesRepository.getFavorites() } returns MutableStateFlow(setOf("1", "2", "3"))
        coEvery { localSettingsRepository.theme } returns MutableStateFlow(ThemeDefaults.THEME_ID)
        coEvery { userRepository.getFavoriteHymnIds() } returns MutableStateFlow(Result.Error("No cloud data"))
        coEvery { userRepository.getUserSettings() } returns MutableStateFlow(Result.Error("No cloud data"))
        coEvery { userRepository.addFavorite(any()) } returns Result.Success(Unit)
        coEvery { userRepository.updateUserSettings(any()) } returns Result.Success(Unit)

        val result = service.migrateLocalDataToCloud()

        assertTrue(result is Result.Success)
        coVerify(exactly = 3) { userRepository.addFavorite(any()) }
    }

    @Test
    fun migrateLocalDataToCloud_withEmptyLocalFavorites_doesNotMigrate() = runTest {
        coEvery { localFavoritesRepository.getFavorites() } returns MutableStateFlow(emptySet())
        coEvery { localSettingsRepository.theme } returns MutableStateFlow(ThemeDefaults.THEME_ID)
        coEvery { userRepository.getFavoriteHymnIds() } returns MutableStateFlow(Result.Error("No cloud data"))
        coEvery { userRepository.getUserSettings() } returns MutableStateFlow(Result.Error("No cloud data"))
        coEvery { userRepository.updateUserSettings(any()) } returns Result.Success(Unit)

        val result = service.migrateLocalDataToCloud()

        assertTrue(result is Result.Success)
        coVerify(exactly = 0) { userRepository.addFavorite(any()) }
    }

    @Test
    fun migrateLocalDataToCloud_mergeFavoritesWithCloudData() = runTest {
        coEvery { localFavoritesRepository.getFavorites() } returns MutableStateFlow(setOf("1", "2"))
        coEvery { localSettingsRepository.theme } returns MutableStateFlow(ThemeDefaults.THEME_ID)
        coEvery { userRepository.getFavoriteHymnIds() } returns MutableStateFlow(Result.Success(setOf("2", "3", "4")))
        coEvery { userRepository.getUserSettings() } returns MutableStateFlow(Result.Success(com.lucasdelima.louveapp.domain.model.UserSettings(themeId = ThemeDefaults.THEME_ID)))
        coEvery { userRepository.addFavorite(any()) } returns Result.Success(Unit)
        coEvery { localFavoritesRepository.addFavorite(any()) } returns Result.Success(Unit)
        coEvery { userRepository.updateUserSettings(any()) } returns Result.Success(Unit)

        val result = service.migrateLocalDataToCloud()

        assertTrue(result is Result.Success)
        coVerify { userRepository.addFavorite("1") }
        coVerify { localFavoritesRepository.addFavorite("3") }
        coVerify { localFavoritesRepository.addFavorite("4") }
    }

    @Test
    fun migrateLocalDataToCloud_whenError_returnsError() = runTest {
        coEvery { localFavoritesRepository.getFavorites() }.throws(RuntimeException("Network error"))

        val result = service.migrateLocalDataToCloud()

        assertTrue(result is Result.Error)
        assertEquals("Falha na sincronização: Network error", (result as Result.Error).message)
    }
}