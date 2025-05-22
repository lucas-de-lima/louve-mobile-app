package com.lucasdelima.louveapp.ui.screens.home


sealed interface HomeUiEvent {
    data class OnHymnClick(val hymnId: Int) : HomeUiEvent
}
