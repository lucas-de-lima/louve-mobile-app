package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.domain.repository.HymnRepository


class FakeHymnRepository : HymnRepository {

    private val hymns = listOf(
        Hymn(
            id = 1,
            number = 101,
            title = "Amazing Grace",
            verses = listOf(
                "Amazing grace! How sweet the sound",
                "That saved a wretch like me!"
            ),
            chorus = null
        )
        // TODO: adicionar mais hinos mockados
    )

    override fun getAllHymns(): List<Hymn> = hymns

    override fun getHymnById(id: Int): Hymn? = hymns.find { it.id == id }
}
