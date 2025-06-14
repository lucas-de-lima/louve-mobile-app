package com.lucasdelima.louveapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.data.repository.FakeHymnRepository // Use a interface se for injetar
import com.lucasdelima.louveapp.domain.model.Hymn // Importe o Hymn do domain
import com.lucasdelima.louveapp.domain.repository.HymnRepository
import kotlinx.coroutines.Job // Para gerenciar o job de busca
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update // Helper para atualizar o StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var originalHymns: List<Hymn> = emptyList()
    private val hymnRepository: HymnRepository = FakeHymnRepository() // Para MVP; idealmente injetado

    private var searchJob: Job? = null

    init {
        loadInitialHymns()
    }

    private fun loadInitialHymns() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            originalHymns = hymnRepository.getAllHymns()
            filterHymns()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            filterHymns()
        }
    }

    private fun filterHymns() {
        val query = _uiState.value.searchQuery
        val filteredDomainHymns = if (query.isBlank()) {
            originalHymns
        } else {
            originalHymns.filter { hymn ->
                hymn.title.contains(query, ignoreCase = true) ||
                        hymn.number.toString().contains(query)
                // Adicione mais campos para busca se necessário (ex: trechos da letra)
            }
        }

        val uiHymns = filteredDomainHymns.map { hymn ->
            HymnUi(id = hymn.id, title = hymn.title, number = hymn.number.toString().padStart(3, '0'))
        }
        _uiState.update { it.copy(hymns = uiHymns, isLoading = false) }
    }
}