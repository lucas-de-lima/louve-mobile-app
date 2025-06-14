package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.domain.model.Hymn
import com.lucasdelima.louveapp.domain.repository.HymnRepository

class FakeHymnRepository : HymnRepository {

    // Agora com uma lista mais completa e variada em português
    private val hymns = listOf(
        Hymn(
            id = 1,
            number = 101,
            title = "Graça Infinita",
            verses = listOf(
                "Sublime graça! Quão doce o som\nQue a um miserável como eu salvou!",
                "Perdido estava, mas me encontrou;\nFui cego, e agora vejo."
            ),
            chorus = "Oh, graça, vem me guiar,\nSeguro em meu lar."
        ),
        Hymn(
            id = 2,
            number = 23,
            title = "Castelo Forte é Nosso Deus",
            verses = listOf(
                "Castelo forte é nosso Deus,\nEspada e bom escudo;",
                "Com seu poder defende os seus,\nEm todo transe agudo."
            ),
            chorus = null // Exemplo de hino sem coro
        ),
        Hymn(
            id = 3,
            number = 15,
            title = "Segurança",
            verses = listOf(
                "Firme nas promessas do meu Salvador,\nCantarei louvores ao meu Criador.",
                "Fico, pela graça, no seu grande amor,\nFirme nas promessas de Jesus."
            ),
            chorus = "Firme, firme,\nFirme nas promessas de Jesus, meu Mestre;\nFirme, firme,\nSim, firme nas promessas de Jesus."
        ),
        Hymn(
            id = 4,
            number = 545,
            title = "Mais Perto Quero Estar",
            verses = listOf(
                "Mais perto quero estar, meu Deus, de Ti!\nAinda que seja a dor que me una a Ti.",
                "Sempre hei de suplicar: Mais perto quero estar;\nMais perto quero estar, meu Deus, de Ti!"
            ),
            chorus = null
        )
    )

    override fun getAllHymns(): List<Hymn> = hymns

    override fun getHymnById(id: Int): Hymn? = hymns.find { it.id == id }
}