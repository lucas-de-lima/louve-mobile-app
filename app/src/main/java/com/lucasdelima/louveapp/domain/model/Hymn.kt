package com.lucasdelima.louveapp.domain.model


data class Hymn(
    val id: Int,
    val number: Int,
    val title: String,
    val verses: List<String>,
    val chorus: String? = null
)
