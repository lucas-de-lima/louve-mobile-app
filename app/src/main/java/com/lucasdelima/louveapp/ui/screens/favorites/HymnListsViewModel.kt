package com.lucasdelima.louveapp.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.domain.model.HymnList
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.HymnListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HymnListsUiState(
    val lists: List<HymnList> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HymnListsViewModel @Inject constructor(
    private val hymnListRepository: HymnListRepository
) : ViewModel() {

    val uiState: StateFlow<HymnListsUiState> = hymnListRepository.getActiveLists()
        .let { flow ->
            flow.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HymnListsUiState(isLoading = true)
            )
        }
}