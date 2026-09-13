package com.lucasdelima.louveapp.domain.model

data class HymnList(
    val id: String,
    val name: String,
    val createdAt: Long,
    val expiresAt: Long? = null,
    val hymnIds: List<String> = emptyList()
)

enum class HymnListType {
    CUSTOM
}