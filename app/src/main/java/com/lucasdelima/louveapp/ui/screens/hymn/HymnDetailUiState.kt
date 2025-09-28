package com.lucasdelima.louveapp.ui.screens.hymn

import com.lucasdelima.louveapp.domain.model.Hymn

data class HymnDetailUiState(
    val isLoading: Boolean = true,
    val hymn: Hymn? = null,
    val error: String? = null,
    val fontScaleFactor: Float = 1.0f,
    val isFavorite: Boolean = false,
    val isUserLoggedIn: Boolean = false
)
