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

    private var currentListId: String = ""

    private val _uiState = MutableStateFlow(HymnListDetailUiState())
    val uiState: StateFlow<HymnListDetailUiState> = _uiState.asStateFlow()

    fun loadList(listId: String) {
        currentListId = listId
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

fun removeHymn(hymnId: Int) {
        viewModelScope.launch {
            hymnListRepository.removeHymnFromList(currentListId, hymnId.toString())
            loadList(currentListId)
        }
    }

    fun moveHymnUp(hymnId: Int) {
        val state = _uiState.value
        val idx = state.hymns.indexOfFirst { it.id == hymnId }
        if (idx <= 0) return
        updateOrderLocally(idx, idx - 1)
    }

    fun moveHymnDown(hymnId: Int) {
        val state = _uiState.value
        val idx = state.hymns.indexOfFirst { it.id == hymnId }
        if (idx < 0 || idx >= state.hymns.size - 1) return
        updateOrderLocally(idx, idx + 1)
    }

    /**
     * Reordena hinos localmente no estado (sem reload) e persiste via repositório.
     * Evita o reload completo que causava ripple de clique no card vizinho.
     */
    private fun updateOrderLocally(fromIdx: Int, toIdx: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            val hymnList = state.hymnList ?: return@launch

            val newHymnIds = hymnList.hymnIds.toMutableList()
            val fromId = newHymnIds[fromIdx]
            newHymnIds[fromIdx] = newHymnIds[toIdx]
            newHymnIds[toIdx] = fromId

            val newHymns = state.hymns.toMutableList()
            val fromHymn = newHymns[fromIdx]
            newHymns[fromIdx] = newHymns[toIdx]
            newHymns[toIdx] = fromHymn

            val updatedList = hymnList.copy(hymnIds = newHymnIds)
            _uiState.update {
                it.copy(hymnList = updatedList, hymns = newHymns)
            }

            hymnListRepository.upsertList(updatedList)
        }
    }
}