package com.lucasdelima.louveapp.ui.screens.home


data class HomeUiState(
    val isLoading: Boolean = true,
    val hymns: List<HymnUi> = emptyList()
)

data class HymnUi(
    val id: Int,
    val title: String,
    val number: String
)
