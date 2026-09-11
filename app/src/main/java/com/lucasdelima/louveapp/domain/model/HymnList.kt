package com.lucasdelima.louveapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class HymnListType {
    CUSTOM
}

@Serializable
data class HymnList(
    val id: String,
    val name: String,
    val type: HymnListType = HymnListType.CUSTOM,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,
    val hymnIds: List<String> = emptyList()
)