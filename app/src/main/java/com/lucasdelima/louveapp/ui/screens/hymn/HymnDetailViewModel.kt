package com.lucasdelima.louveapp.ui.screens.hymn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.data.repository.FakeHymnRepository
import com.lucasdelima.louveapp.data.repository.HymnRepositoryImpl
import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.domain.repository.HymnRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Defina o State aqui ou em um arquivo separado (ex: HymnDetailContract.kt)
data class HymnDetailUIState(
    val isLoading: Boolean = true,
    val hymn: Hymn? = null,
    val error: String? = null,
    val fontScaleFactor: Float = 1.0f
)

class HymnDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val hymnId: Int? = savedStateHandle["id"]
    private val hymnRepository: HymnRepository = HymnRepositoryImpl()

    private val _uiState = MutableStateFlow(HymnDetailUIState())
    val uiState: StateFlow<HymnDetailUIState> = _uiState.asStateFlow()

    init {
        loadHymnDetails()
    }

    private fun loadHymnDetails() {
        if (hymnId == null) {
            _uiState.update { it.copy(isLoading = false, error = "ID do hino não foi encontrado na navegação.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val hymn = hymnRepository.getHymnById(hymnId)
                if (hymn != null) {
                    _uiState.update { it.copy(isLoading = false, hymn = hymn, error = null) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Hino não encontrado.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Erro ao carregar o hino.") }
            }
        }
    }

    fun increaseFontSize() {
        _uiState.update { it.copy(fontScaleFactor = minOf(2.0f, it.fontScaleFactor + 0.1f)) }
    }

    fun decreaseFontSize() {
        _uiState.update { it.copy(fontScaleFactor = maxOf(0.5f, it.fontScaleFactor - 0.1f)) }
    }
}