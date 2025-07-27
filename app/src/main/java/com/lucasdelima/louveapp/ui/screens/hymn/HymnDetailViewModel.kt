package com.lucasdelima.louveapp.ui.screens.hymn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.model.UserProfile
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.FavoritesRepository
import com.lucasdelima.louveapp.domain.repository.HymnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HymnDetailViewModel @Inject constructor(
    private val hymnRepository: HymnRepository,
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val hymnId: String = savedStateHandle.get<Int>("id")!!.toString()
    private val _uiState = MutableStateFlow(HymnDetailUiState())
    val uiState: StateFlow<HymnDetailUiState> = _uiState.asStateFlow()

    // Notificar usuario caso salvar nos favoritos falhe
    private val _eventFlow = MutableSharedFlow<HymnDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
    sealed class HymnDetailEvent {
        data class ShowSnackbar(val message: String) : HymnDetailEvent()
    }

    init {
        // MODIFICAÇÃO: A lógica de carregamento agora é reativa.
        observeHymnDetails()
    }

    private fun observeHymnDetails() {
        // CORREÇÃO: Declaramos cada Flow em sua própria variável com tipo explícito.
        // Isso elimina a ambiguidade para o compilador.
        val userFlow: Flow<UserProfile?> = authRepository.getCurrentUser()
        val favoritesFlow: Flow<Result<Set<String>>> = favoritesRepository.getFavoriteHymnIds()
        // Como `getHymnById` é uma suspend fun, nós a envolvemos em um
        // construtor `flow` para criar um Flow que emite o valor uma única vez.
        val hymnFlow: Flow<Hymn?> = flow {
            emit(hymnRepository.getHymnById(hymnId.toInt()))
        }

        // Agora, a chamada ao `combine` funciona, pois os tipos dos parâmetros são Flows.
        combine(userFlow, favoritesFlow, hymnFlow) { user, favoritesResult, hymn ->
            val isUserLoggedIn = user != null
            val favoriteIds = (favoritesResult as? Result.Success)?.data ?: emptySet()

            _uiState.value.copy(
                isLoading = false,
                hymn = hymn,
                isUserLoggedIn = isUserLoggedIn,
                isFavorite = favoriteIds.contains(hymnId),
                error = if (hymn == null) "Hino não encontrado." else null
            )
        }
            .onEach { newState ->
                _uiState.value = newState
            }
            .launchIn(viewModelScope)
    }


    fun onToggleFavorite() {
        val isCurrentlyFavorite = _uiState.value.isFavorite
        val newFavoriteState = !isCurrentlyFavorite

        _uiState.value = _uiState.value.copy(isFavorite = newFavoriteState)

        viewModelScope.launch {
            // MODIFICAÇÃO: A chamada é a mesma, a lógica de roteamento está no repositório.
            val result = if (newFavoriteState) {
                favoritesRepository.addFavorite(hymnId)
            } else {
                favoritesRepository.removeFavorite(hymnId)
            }
            if (result is Result.Error) {
                _uiState.update { it.copy(isFavorite = isCurrentlyFavorite) }
                // Envia um evento para a UI
                _eventFlow.emit(HymnDetailEvent.ShowSnackbar("Falha ao salvar favorito. Tente novamente."))
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
