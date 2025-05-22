package com.lucasdelima.louveapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdelima.louveapp.data.repository.FakeHymnRepository
import com.lucasdelima.louveapp.domain.repository.HymnRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState
    private val hymnRepository: HymnRepository = FakeHymnRepository()

    init {
        loadHymns()
    }

    private fun loadHymns() {
        viewModelScope.launch {
            delay(1000)
            val domainHymns = hymnRepository.getAllHymns()
            val uiHymns = domainHymns.map { hymn ->
                HymnUi(id = hymn.id, title = hymn.title, number = hymn.number.toString().padStart(3, '0'))
            }
            _uiState.value = HomeUiState(isLoading = false, hymns = uiHymns)
        }
    }
}
