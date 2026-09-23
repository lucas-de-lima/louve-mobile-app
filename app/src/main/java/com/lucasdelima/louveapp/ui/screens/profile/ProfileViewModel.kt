package com.lucasdelima.louveapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.AnalyticsService
import com.lucasdelima.louveapp.domain.repository.FavoritesRepository
import com.lucasdelima.louveapp.domain.repository.HymnListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileStatsUiState(
    val favoritesCount: Int = 0,
    val hymnListsCount: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val analyticsService: AnalyticsService,
    private val favoritesRepository: FavoritesRepository,
    private val hymnListRepository: HymnListRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(ProfileStatsUiState())
    val stats: StateFlow<ProfileStatsUiState> = _stats.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val favoriteIds = (favoritesRepository.getFavoriteHymnIds().first() as? Result.Success)?.data ?: emptySet()
            val lists = hymnListRepository.getActiveLists().first()
            _stats.value = ProfileStatsUiState(
                favoritesCount = favoriteIds.size,
                hymnListsCount = lists.size
            )
        }
    }

    fun trackUserLogout() {
        analyticsService.trackUserLogout()
    }

    fun trackScreenView() {
        analyticsService.trackScreenView("ProfileScreen")
    }

    fun trackLoginAttempt() {
        analyticsService.trackUserLogin()
    }
}
