package com.lucasdelima.louveapp.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.FavoritesRepository
import com.lucasdelima.louveapp.domain.repository.HymnRepository
import com.lucasdelima.louveapp.ui.screens.home.HymnUi
import com.lucasdelima.louveapp.ui.screens.home.toHymnUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favoriteHymns: List<HymnUi> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val hymnRepository: HymnRepository
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = run {
        val favoritesFlow: Flow<Result<Set<String>>> = favoritesRepository.getFavoriteHymnIds()
        val allHymns: List<Hymn> = hymnRepository.getAllHymns()

        favoritesFlow.map { favoritesResult ->
            when (favoritesResult) {
                is Result.Success -> {
                    val favoriteIds = favoritesResult.data
                    val favoriteDomainHymns = allHymns.filter { hymn ->
                        hymn.id.toString() in favoriteIds
                    }
                    val favoriteUiHymns = favoriteDomainHymns.map { it.toHymnUi() }
                    FavoritesUiState(isLoading = false, favoriteHymns = favoriteUiHymns)
                }
                is Result.Error -> {
                    FavoritesUiState(isLoading = false, error = favoritesResult.message)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesUiState()
        )
    }
}
