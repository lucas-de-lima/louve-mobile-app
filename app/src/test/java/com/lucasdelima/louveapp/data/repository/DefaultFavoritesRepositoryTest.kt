package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.LocalFavoritesRepository
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class DefaultFavoritesRepositoryTest {

    private val authRepository: AuthRepository = mockk()
    private val localRepository: LocalFavoritesRepository = mockk()
    private val remoteRepository: UserRepository = mockk()
    private lateinit var repository: DefaultFavoritesRepository

    @Before
    fun setup() {
        repository = DefaultFavoritesRepository(
            authRepository,
            localRepository,
            remoteRepository
        )
    }

    @Test
    fun getFavoriteHymnIds_returnsLocalFavoritesAsSuccess() = runTest {
        every { authRepository.getCurrentUser() } returns MutableStateFlow(null)
        every { localRepository.getFavorites() } returns MutableStateFlow(setOf("1", "2", "3"))

        val result = repository.getFavoriteHymnIds().first()

        assertTrue(result is Result.Success)
        assertEquals(setOf("1", "2", "3"), (result as Result.Success).data)
    }

    @Test
    fun getFavoriteHymnIds_returnsEmptySet_whenNoFavorites() = runTest {
        every { authRepository.getCurrentUser() } returns MutableStateFlow(null)
        every { localRepository.getFavorites() } returns MutableStateFlow(emptySet())

        val result = repository.getFavoriteHymnIds().first()

        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    @Test
    fun addFavorite_delegatesToLocalRepository() = runTest {
        coEvery { localRepository.addFavorite("42") } returns Result.Success(Unit)

        val result = repository.addFavorite("42")

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { localRepository.addFavorite("42") }
    }

    @Test
    fun removeFavorite_delegatesToLocalRepository() = runTest {
        coEvery { localRepository.removeFavorite("42") } returns Result.Success(Unit)

        val result = repository.removeFavorite("42")

        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { localRepository.removeFavorite("42") }
    }

    @Test
    fun syncWhenOnline_withLoggedUser_returnsSuccess() = runTest {
        every { authRepository.getCurrentUser() } returns MutableStateFlow(com.lucasdelima.louveapp.domain.model.UserProfile("uid", "User", "user@test.com", null))

        val result = repository.syncWhenOnline()

        assertTrue(result is Result.Success)
    }

    @Test
    fun syncWhenOnline_withoutUser_returnsSuccess() = runTest {
        every { authRepository.getCurrentUser() } returns MutableStateFlow(null)

        val result = repository.syncWhenOnline()

        assertTrue(result is Result.Success)
    }

    @Test
    fun syncWhenOnline_whenException_returnsError() = runTest {
        every { authRepository.getCurrentUser() }.throws(RuntimeException("Network failed"))

        val result = repository.syncWhenOnline()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Network failed"))
    }

    @Test
    fun checkForConflicts_returnsFalse() = runTest {
        every { authRepository.getCurrentUser() } returns MutableStateFlow(null)

        val result = repository.checkForConflicts()

        assertFalse(result)
    }

    @Test
    fun resolveConflicts_returnsSuccess() = runTest {
        every { authRepository.getCurrentUser() } returns MutableStateFlow(null)

        val result = repository.resolveConflicts()

        assertTrue(result is Result.Success)
    }
}