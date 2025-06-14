package com.lucasdelima.louveapp.domain.repository

import com.lucasdelima.louveapp.domain.model.Hymn


interface HymnRepository {
    fun getAllHymns(): List<Hymn>
    fun getHymnById(id: Int): Hymn?
}
