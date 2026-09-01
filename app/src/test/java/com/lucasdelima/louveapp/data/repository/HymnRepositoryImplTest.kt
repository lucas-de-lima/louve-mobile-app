package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.domain.model.Hymn
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

class HymnRepositoryImplTest {

    private lateinit var repository: HymnRepositoryImpl

    @Before
    fun setup() {
        repository = HymnRepositoryImpl()
    }

    @Test
    fun getAllHymns_returnsAll640Hymns() {
        assertEquals(640, repository.getAllHymns().size)
    }

    @Test
    fun getAllHymns_firstHymnIsChuvasDeGraca() {
        val first = repository.getAllHymns().first()
        assertEquals(1, first.id)
        assertEquals(1, first.number)
        assertEquals("Chuvas de Graça", first.title)
    }

    @Test
    fun getAllHymns_hymnsAreOrderedById() {
        val hymns = repository.getAllHymns()
        for (i in 1 until hymns.size) {
            assertTrue(
                "Hymn at index $i (id=${hymns[i].id}) should have id > previous (id=${hymns[i-1].id})",
                hymns[i].id > hymns[i-1].id
            )
        }
    }

    @Test
    fun getHymnById_withValidId_returnsHymn() {
        val hymn = repository.getHymnById(1)
        assertNotNull(hymn)
        assertEquals("Chuvas de Graça", hymn?.title)
    }

    @Test
    fun getHymnById_withLastId_returnsLastHymn() {
        val hymn = repository.getHymnById(640)
        assertNotNull(hymn)
        assertEquals(640, hymn?.id)
    }

    @Test
    fun getHymnById_withInvalidId_returnsNull() {
        assertNull(repository.getHymnById(0))
        assertNull(repository.getHymnById(-1))
        assertNull(repository.getHymnById(641))
    }

    @Test
    fun getHymnById_retrievesHymnWithCompleteStructure() {
        val hymn = repository.getHymnById(1)
        assertNotNull(hymn)
        assertEquals(1, hymn?.id)
        assertEquals(1, hymn?.number)
        assertEquals("Chuvas de Graça", hymn?.title)
        assertTrue(hymn?.verses?.isNotEmpty() == true)
        assertNotNull(hymn?.chorus)
    }

    @Test
    fun getAllHymns_everyHymnHasRequiredFields() {
        val hymns = repository.getAllHymns()
        hymns.forEach { hymn ->
            assertTrue("Hymn ${hymn.id} has valid id", hymn.id > 0)
            assertTrue("Hymn ${hymn.id} has valid number", hymn.number > 0)
            assertTrue("Hymn ${hymn.id} has non-blank title", hymn.title.isNotBlank())
            assertTrue("Hymn ${hymn.id} has verses", hymn.verses.isNotEmpty())
        }
    }
}