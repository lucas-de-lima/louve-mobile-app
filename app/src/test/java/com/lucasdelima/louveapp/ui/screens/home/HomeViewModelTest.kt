package com.lucasdelima.louveapp.ui.screens.home

import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.domain.repository.HymnRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class HomeViewModelTest {

    private val hymnRepository: HymnRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    private val sampleHymns = listOf(
        Hymn(1, 1, "Jesus Cristo", listOf("Verso 1", "Verso 2"), "Coro"),
        Hymn(2, 2, "Fé e Esperança", listOf("Verso único"), "Aleluia"),
        Hymn(3, 3, "Amor de Deus", listOf("Deus é amor"), "Amém"),
        Hymn(4, 45, "Santo Espírito", listOf("Vem sobre nós"), "Vem Espírito"),
        Hymn(5, 120, "Paz do Senhor", listOf("A paz de Cristo"), "Paz")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { hymnRepository.getAllHymns() } returns sampleHymns
        viewModel = HomeViewModel(hymnRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialLoad_showsAllHymns() {
        assertEquals(5, viewModel.uiState.value.hymns.size)
        assertEquals("001", viewModel.uiState.value.hymns[0].number)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun searchByTitle_filtersCorrectly() = runTest {
        viewModel.onSearchQueryChanged("jesus")
        advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        val filtered = viewModel.uiState.value.hymns
        assertEquals(1, filtered.size)
        assertEquals("Jesus Cristo", filtered[0].title)
    }

    @Test
    fun searchByPartialTitle_findsMatch() = runTest {
        viewModel.onSearchQueryChanged("esperança")
        advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        val filtered = viewModel.uiState.value.hymns
        assertEquals(1, filtered.size)
        assertEquals("Fé e Esperança", filtered[0].title)
    }

    @Test
    fun searchByAccentedQuery_matchesNormalizedText() = runTest {
        viewModel.onSearchQueryChanged("fé")
        advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        val filtered = viewModel.uiState.value.hymns
        assertEquals(1, filtered.size)
        assertEquals("Fé e Esperança", filtered[0].title)
    }

    @Test
    fun searchByHymnNumber_findsMatch() = runTest {
        viewModel.onSearchQueryChanged("045")
        advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        val filtered = viewModel.uiState.value.hymns
        assertEquals(1, filtered.size)
        assertEquals(45, sampleHymns[3].number)
    }

    @Test
    fun searchByChorus_findsMatch() = runTest {
        viewModel.onSearchQueryChanged("aleluia")
        advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        val filtered = viewModel.uiState.value.hymns
        assertEquals(1, filtered.size)
        assertEquals("Fé e Esperança", filtered[0].title)
    }

    @Test
    fun searchByMultipleWords_matchesAll() = runTest {
        viewModel.onSearchQueryChanged("jesus cristo")
        advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        val filtered = viewModel.uiState.value.hymns
        assertEquals(1, filtered.size)
    }

    @Test
    fun searchByNonExistentWord_returnsEmpty() = runTest {
        viewModel.onSearchQueryChanged("xyz123")
        advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        val filtered = viewModel.uiState.value.hymns
        assertEquals(0, filtered.size)
    }

    @Test
    fun clearSearchShowsAllHymns() = runTest {
        viewModel.onSearchQueryChanged("jesus")
        advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.hymns.size)

        viewModel.onSearchQueryChanged("")
        advanceTimeBy(400)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(5, viewModel.uiState.value.hymns.size)
    }

    @Test
    fun debounce_onlyFiresAfterDelay() = runTest {
        viewModel.onSearchQueryChanged("j")
        advanceTimeBy(100)
        viewModel.onSearchQueryChanged("je")
        advanceTimeBy(100)
        viewModel.onSearchQueryChanged("jes")
        advanceTimeBy(100)
        viewModel.onSearchQueryChanged("jesus")
        advanceTimeBy(100)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(5, viewModel.uiState.value.hymns.size)

        advanceTimeBy(300)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.hymns.size)
    }
}