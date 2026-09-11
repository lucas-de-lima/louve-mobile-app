package com.lucasdelima.louveapp.domain.repository

import com.lucasdelima.louveapp.domain.model.HymnList
import com.lucasdelima.louveapp.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface HymnListRepository {
    fun getAllLists(): Flow<List<HymnList>>
    fun getActiveLists(): Flow<List<HymnList>>
    suspend fun createList(name: String, expiresAt: Long?): Result<String>
    suspend fun renameList(id: String, name: String): Result<Unit>
    suspend fun updateExpiration(id: String, expiresAt: Long?): Result<Unit>
    suspend fun deleteList(id: String): Result<Unit>
    suspend fun addHymnToList(listId: String, hymnId: String): Result<Unit>
    suspend fun removeHymnFromList(listId: String, hymnId: String): Result<Unit>
    suspend fun cleanupExpiredLists(): Result<Unit>
}