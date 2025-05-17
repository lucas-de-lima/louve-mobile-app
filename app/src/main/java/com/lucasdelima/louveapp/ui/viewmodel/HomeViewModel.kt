package com.lucasdelima.louveapp.ui.viewmodel


import androidx.lifecycle.ViewModel
import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.domain.repository.HymnRepository

class HomeViewModel(
    private val repository: HymnRepository
) : ViewModel() {

    val hymns: List<Hymn> = repository.getAllHymns()

    // TODO: adicionar lógica de filtro por texto
}
