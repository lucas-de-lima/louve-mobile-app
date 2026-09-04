package com.lucasdelima.louveapp.ui.screens.hymn

import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.FavoritesRepository
import com.lucasdelima.louveapp.domain.repository.HymnRepository
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

class HymnDetailViewModelTest {

    private val hymnRepository: HymnRepository = mockk()
    private val favoritesRepository: FavoritesRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HymnDetailViewModel

    private val sampleHymn = Hymn(1, 1, "Jesus Cristo", listOf("Verso 1", "Verso 2"), "Coro")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { authRepository.getCurrentUser() } returns MutableStateFlow(null)
        every { settingsRepository.fontScaleFactor } returns MutableStateFlow(1.0f)
        coEvery { settingsRepository.saveFontScaleFactor(any()) } returns Unit
        every { hymnRepository.getHymnById(1) } returns sampleHymn
        every { favoritesRepository.getFavoriteHymnIds() } returns MutableStateFlow(Result.Success(emptySet()))
        viewModel = HymnDetailViewModel(hymnRepository, favoritesRepository, authRepository, settingsRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isLoading() {
        assertEquals(true, viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.hymn)
        assertEquals(1.0f, viewModel.uiState.value.fontScaleFactor, 0.001f)
        assertFalse(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun setHymnId_loadsHymnDetails() {
        viewModel.setHymnId(1)
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(false, isLoading)
            assertEquals(sampleHymn, hymn)
            assertEquals("Jesus Cristo", hymn?.title)
            assertNull(error)
        }
    }

    @Test
    fun setHymnId_withInvalidId_showsError() {
        every { hymnRepository.getHymnById(999) } returns null
        viewModel.setHymnId(999)
        testDispatcher.scheduler.advanceUntilIdle()

        with(viewModel.uiState.value) {
            assertEquals(false, isLoading)
            assertNull(hymn)
            assertEquals("Hino não encontrado.", error)
        }
    }

    @Test
    fun setHymnId_whenFavorite_showsFavoriteState() {
        every { favoritesRepository.getFavoriteHymnIds() } returns MutableStateFlow(Result.Success(setOf("1")))
        viewModel = HymnDetailViewModel(hymnRepository, favoritesRepository, authRepository, settingsRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setHymnId(1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, viewModel.uiState.value.isFavorite)
    }

    @Test
    fun onToggleFavorite_addsFavorite() = runTest {
        viewModel.setHymnId(1)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { favoritesRepository.addFavorite("1") } returns Result.Success(Unit)

        viewModel.onToggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { favoritesRepository.addFavorite("1") }
        assertEquals(true, viewModel.uiState.value.isFavorite)
    }

    @Test
    fun onToggleFavorite_removesFavorite() = runTest {
        every { favoritesRepository.getFavoriteHymnIds() } returns MutableStateFlow(Result.Success(setOf("1")))
        viewModel = HymnDetailViewModel(hymnRepository, favoritesRepository, authRepository, settingsRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setHymnId(1)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { favoritesRepository.removeFavorite("1") } returns Result.Success(Unit)

        viewModel.onToggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { favoritesRepository.removeFavorite("1") }
        assertEquals(false, viewModel.uiState.value.isFavorite)
    }

    @Test
    fun onToggleFavorite_onError_revertsState() = runTest {
        viewModel.setHymnId(1)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { favoritesRepository.addFavorite("1") } returns Result.Error("Failed")

        viewModel.onToggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.isFavorite)
    }

    @Test
    fun increaseFontSize_increasesFont() {
        viewModel.increaseFontSize()

        assertEquals(1.1f, viewModel.uiState.value.fontScaleFactor, 0.001f)
    }

    @Test
    fun increaseFontSize_capsAt2x() {
        repeat(15) { viewModel.increaseFontSize() }

        assertEquals(2.0f, viewModel.uiState.value.fontScaleFactor, 0.001f)
    }

    @Test
    fun decreaseFontSize_decreasesFont() {
        repeat(5) { viewModel.increaseFontSize() }
        viewModel.decreaseFontSize()

        assertEquals(1.4f, viewModel.uiState.value.fontScaleFactor, 0.001f)
    }

    @Test
    fun decreaseFontSize_floorAtHalf() {
        repeat(10) { viewModel.decreaseFontSize() }

        assertEquals(0.5f, viewModel.uiState.value.fontScaleFactor, 0.001f)
    }

    @Test
    fun increaseFontSize_persistsToSettings() = runTest {
        viewModel.increaseFontSize()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsRepository.saveFontScaleFactor(1.1f) }
    }

    @Test
    fun decreaseFontSize_persistsToSettings() = runTest {
        viewModel.decreaseFontSize()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { settingsRepository.saveFontScaleFactor(0.9f) }
    }

    @Test
    fun setHymnId_showsUserLoggedInState() {
        val user = com.lucasdelima.louveapp.domain.model.UserProfile("uid", "User", "user@test.com", null)
        every { authRepository.getCurrentUser() } returns MutableStateFlow(user)
        viewModel = HymnDetailViewModel(hymnRepository, favoritesRepository, authRepository, settingsRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.setHymnId(1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(true, viewModel.uiState.value.isUserLoggedIn)
    }

    @Test
    fun fontScaleFactor_observesSettings() {
        every { settingsRepository.fontScaleFactor } returns MutableStateFlow(1.5f)
        viewModel = HymnDetailViewModel(hymnRepository, favoritesRepository, authRepository, settingsRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1.5f, viewModel.uiState.value.fontScaleFactor, 0.001f)
    }
}