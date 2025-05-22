package com.lucasdelima.louveapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadHymns()
    }

    private fun loadHymns() {
        viewModelScope.launch {
            delay(1000)
            _uiState.value = HomeUiState(
                isLoading = false,
                hymns = listOf(
                    HymnUi(id = 1, title = "Graça Infinita", number = "001"),
                    HymnUi(id = 2, title = "Cristo Vive", number = "002"),
                    HymnUi(id = 3, title = "Aleluia", number = "003")
                )
            )
        }
    }
}
