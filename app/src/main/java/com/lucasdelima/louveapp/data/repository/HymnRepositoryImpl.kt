package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.data.datasource.HymnDataSource
import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.domain.repository.HymnRepository

class HymnRepositoryImpl : HymnRepository {
    private val hymns: List<Hymn> = HymnDataSource.allHymns

    override fun getAllHymns(): List<Hymn> = hymns

    override fun getHymnById(id: Int): Hymn? = hymns.find { it.id == id }
}