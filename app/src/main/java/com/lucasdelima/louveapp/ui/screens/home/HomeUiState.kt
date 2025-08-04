package com.lucasdelima.louveapp.ui.screens.home

import com.lucasdelima.louveapp.domain.model.Hymn


data class HomeUiState(
    val isLoading: Boolean = true,
    val hymns: List<HymnUi> = emptyList(),
    val searchQuery: String = ""
)

data class HymnUi(
    val id: Int,
    val title: String,
    val number: String
)

fun Hymn.toHymnUi(): HymnUi {
    return HymnUi(
        id = this.id,
        number = this.number.toString().padStart(3, '0'),
        title = this.title
    )
}