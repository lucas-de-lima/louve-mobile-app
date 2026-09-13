package com.lucasdelima.louveapp.ui.screens.hymnlistdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.domain.repository.HymnListRepository
import com.lucasdelima.louveapp.domain.repository.HymnRepository
import com.lucasdelima.louveapp.ui.screens.home.toHymnUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HymnListDetailViewModel @Inject constructor(
    private val hymnListRepository: HymnListRepository,
    private val hymnRepository: HymnRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HymnListDetailUiState())
    val uiState: StateFlow<HymnListDetailUiState> = _uiState.asStateFlow()

    fun loadList(listId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val lists = hymnListRepository.getAllLists().first()
            val list = lists.find { it.id == listId }
            if (list == null) {
                _uiState.update { it.copy(isLoading = false, error = "Lista não encontrada") }
                return@launch
            }
            val allHymns = hymnRepository.getAllHymns()
            val hymnUis = list.hymnIds.mapNotNull { id ->
                allHymns.find { it.id.toString() == id }?.toHymnUi()
            }
            _uiState.update {
                it.copy(isLoading = false, hymnList = list, hymns = hymnUis)
            }
        }
    }
}