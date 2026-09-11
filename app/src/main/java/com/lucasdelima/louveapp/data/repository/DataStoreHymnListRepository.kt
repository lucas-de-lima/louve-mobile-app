package com.lucasdelima.louveapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lucasdelima.louveapp.domain.model.HymnList
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.HymnListRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "louve_hymn_lists")

@Singleton
class DataStoreHymnListRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : HymnListRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private object Keys {
        val HYMN_LISTS = stringPreferencesKey("hymn_lists_json")
    }

    override fun getAllLists(): Flow<List<HymnList>> {
        return context.dataStore.data.map { prefs ->
            val raw = prefs[Keys.HYMN_LISTS] ?: "[]"
            deserialize(raw)
        }
    }

    override fun getActiveLists(): Flow<List<HymnList>> {
        return getAllLists().map { lists ->
            val now = System.currentTimeMillis()
            lists.filter { it.expiresAt == null || it.expiresAt > now }
        }
    }

    override suspend fun createList(name: String, expiresAt: Long?): Result<String> {
        return try {
            val id = UUID.randomUUID().toString()
            val newList = HymnList(
                id = id,
                name = name,
                expiresAt = expiresAt,
                hymnIds = emptyList()
            )
            context.dataStore.edit { prefs ->
                val existing = deserialize(prefs[Keys.HYMN_LISTS] ?: "[]")
                val updated = existing + newList
                prefs[Keys.HYMN_LISTS] = serialize(updated)
            }
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error("Falha ao criar lista", e)
        }
    }

    override suspend fun renameList(id: String, name: String): Result<Unit> {
        return updateLists { lists ->
            lists.map { if (it.id == id) it.copy(name = name) else it }
        }
    }

    override suspend fun updateExpiration(id: String, expiresAt: Long?): Result<Unit> {
        return updateLists { lists ->
            lists.map { if (it.id == id) it.copy(expiresAt = expiresAt) else it }
        }
    }

    override suspend fun deleteList(id: String): Result<Unit> {
        return updateLists { lists ->
            lists.filter { it.id != id }
        }
    }

    override suspend fun addHymnToList(listId: String, hymnId: String): Result<Unit> {
        return updateLists { lists ->
            lists.map { list ->
                if (list.id == listId && hymnId !in list.hymnIds) {
                    list.copy(hymnIds = list.hymnIds + hymnId)
                } else list
            }
        }
    }

    override suspend fun removeHymnFromList(listId: String, hymnId: String): Result<Unit> {
        return updateLists { lists ->
            lists.map { list ->
                if (list.id == listId) {
                    list.copy(hymnIds = list.hymnIds - hymnId)
                } else list
            }
        }
    }

    override suspend fun cleanupExpiredLists(): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            context.dataStore.edit { prefs ->
                val existing = deserialize(prefs[Keys.HYMN_LISTS] ?: "[]")
                val active = existing.filter { it.expiresAt == null || it.expiresAt > now }
                prefs[Keys.HYMN_LISTS] = serialize(active)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Falha na limpeza de listas expiradas", e)
        }
    }

    private suspend fun updateLists(transform: (List<HymnList>) -> List<HymnList>): Result<Unit> {
        return try {
            context.dataStore.edit { prefs ->
                val existing = deserialize(prefs[Keys.HYMN_LISTS] ?: "[]")
                prefs[Keys.HYMN_LISTS] = serialize(transform(existing))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Falha ao atualizar listas", e)
        }
    }

    private fun deserialize(raw: String): List<HymnList> {
        return try {
            json.decodeFromString<List<HymnList>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun serialize(lists: List<HymnList>): String {
        return json.encodeToString(lists)
    }
}