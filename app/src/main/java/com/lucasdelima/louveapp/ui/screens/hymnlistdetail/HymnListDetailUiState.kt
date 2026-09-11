package com.lucasdelima.louveapp.ui.screens.hymnlistdetail

import com.lucasdelima.louveapp.domain.model.HymnList
import com.lucasdelima.louveapp.ui.screens.home.HymnUi

data class HymnListDetailUiState(
    val isLoading: Boolean = true,
    val hymnList: HymnList? = null,
    val hymns: List<HymnUi> = emptyList(),
    val error: String? = null
)