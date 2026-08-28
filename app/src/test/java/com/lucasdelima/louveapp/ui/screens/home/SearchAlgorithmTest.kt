package com.lucasdelima.louveapp.ui.screens.home

import org.junit.Test
import org.junit.Assert.*

class SearchAlgorithmTest {

    @Test
    fun unaccent_removesAccents() {
        assertEquals("cancao", "canção".unaccent())
        assertEquals("hino", "híno".unaccent())
        assertEquals("jesus", "jesus".unaccent())
        assertEquals("fe", "fé".unaccent())
        assertEquals("agua", "água".unaccent())
    }

    @Test
    fun unaccent_handlesEmptyString() {
        assertEquals("", "".unaccent())
    }

    @Test
    fun unaccent_preservesNonAccentedChars() {
        assertEquals("hello world", "hello world".unaccent())
        assertEquals("123", "123".unaccent())
    }

    @Test
    fun normalizeForSearch_removesAccents() {
        assertEquals("cancao", "canção".normalizeForSearch())
    }

    @Test
    fun normalizeForSearch_lowercases() {
        assertEquals("jesus", "JESUS".normalizeForSearch())
        assertEquals("aleluia", "Aleluia".normalizeForSearch())
    }

    @Test
    fun normalizeForSearch_removesPunctuation() {
        assertEquals("senhor", "Senhor,".normalizeForSearch())
        assertEquals("amem", "amém!".normalizeForSearch())
        assertEquals("gloria", "glória?".normalizeForSearch())
        assertEquals("paz", "paz;".normalizeForSearch())
        assertEquals("fe", "fé:".normalizeForSearch())
    }

    @Test
    fun normalizeForSearch_handlesCombinedTransforms() {
        assertEquals("jesus cristo", "Jesus Cristo,".normalizeForSearch())
        assertEquals("fe esperanca", "Fé, esperança!".normalizeForSearch())
    }

    @Test
    fun normalizeForSearch_handlesEmptyString() {
        assertEquals("", "".normalizeForSearch())
    }
}