package com.lucasdelima.louveapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.data.repository.FakeHymnRepository // Use a interface se for injetar
import com.lucasdelima.louveapp.data.repository.HymnRepositoryImpl
import com.lucasdelima.louveapp.domain.model.Hymn // Importe o Hymn do domain
import com.lucasdelima.louveapp.domain.repository.HymnRepository
import kotlinx.coroutines.Job // Para gerenciar o job de busca
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update // Helper para atualizar o StateFlow
import kotlinx.coroutines.launch


private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun CharSequence.unaccent(): String {
    val temp = java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
    return REGEX_UNACCENT.replace(temp, "")
}

fun String.normalizeForSearch(): String {
    // Primeiro removemos os acentos
    val unaccented = this.unaccent()
    // Depois, removemos pontuações comuns e convertemos para minúsculas
    return unaccented.lowercase().replace(Regex("[.,!?;:]"), "")
}

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var originalHymns: List<Hymn> = emptyList()
    private val hymnRepository: HymnRepository = HymnRepositoryImpl()

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
            // 1. Normaliza a busca do usuário e a quebra em palavras
            val queryWords = query.normalizeForSearch().split(' ').filter { it.isNotBlank() }

            originalHymns.filter { hymn ->
                // 2. Cria um "super texto" do hino normalizado
                val hymnSearchableContent = (
                        hymn.title + " " +
                                hymn.number.toString().padStart(3, '0') + " " + // Adiciona o número formatado para a busca
                                hymn.verses.joinToString(" ") + " " +
                                hymn.chorus
                        ).normalizeForSearch()

                // 3. Verifica se TODAS as palavras da busca existem no conteúdo do hino
                queryWords.all { word -> hymnSearchableContent.contains(word) }
            }
        }

        val uiHymns = filteredDomainHymns.map { hymn ->
            HymnUi(id = hymn.id, title = hymn.title, number = hymn.number.toString().padStart(3, '0'))
        }
        _uiState.update { it.copy(hymns = uiHymns, isLoading = false) }
    }
}