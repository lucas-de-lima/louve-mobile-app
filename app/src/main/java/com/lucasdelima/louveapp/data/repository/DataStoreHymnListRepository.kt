package com.lucasdelima.louveapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lucasdelima.louveapp.domain.model.HymnList
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.HymnListRepository
import com.lucasdelima.louveapp.domain.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "louve_hymn_lists")

@Singleton
class DataStoreHymnListRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val applicationScope: CoroutineScope
) : HymnListRepository {

    private object Keys {
        val HYMN_LISTS = stringPreferencesKey("hymn_lists_json")
        val LIST_ORDER = stringPreferencesKey("list_order")

        fun listKey(id: String) = stringPreferencesKey("list_$id")
    }

    override fun getAllLists(): Flow<List<HymnList>> {
        return context.dataStore.data.map { prefs ->
            readLists(prefs)
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
                createdAt = System.currentTimeMillis(),
                expiresAt = expiresAt,
                hymnIds = emptyList(),
                updatedAt = System.currentTimeMillis()
            )
            context.dataStore.edit { prefs ->
                migrateLegacyIfNeeded(prefs)
                val existing = readLists(prefs)
                writeLists(prefs, existing + newList)
            }
            syncInBackground { userRepository.upsertHymnList(newList) }
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error("Falha ao criar lista", e)
        }
    }

    override suspend fun renameList(id: String, name: String): Result<Unit> {
        return updateList(id) { it.copy(name = name) }.also { syncListInBackground(id) }
    }

    override suspend fun updateExpiration(id: String, expiresAt: Long?): Result<Unit> {
        return updateList(id) { it.copy(expiresAt = expiresAt) }.also { syncListInBackground(id) }
    }

    override suspend fun deleteList(id: String): Result<Unit> {
        val result = updateLists { lists -> lists.filter { it.id != id } }
        if (result is Result.Success) syncInBackground { userRepository.deleteHymnList(id) }
        return result
    }

    override suspend fun addHymnToList(listId: String, hymnId: String): Result<Unit> {
        return updateList(listId) { list ->
                if (list.id == listId && hymnId !in list.hymnIds) {
                    list.copy(hymnIds = list.hymnIds + hymnId)
                } else list
            }.also { syncListInBackground(listId) }
    }

    override suspend fun removeHymnFromList(listId: String, hymnId: String): Result<Unit> {
        return updateList(listId) { list ->
                if (list.id == listId) {
                    list.copy(hymnIds = list.hymnIds - hymnId)
                } else list
            }.also { syncListInBackground(listId) }
    }

    override suspend fun cleanupExpiredLists(): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            context.dataStore.edit { prefs ->
                migrateLegacyIfNeeded(prefs)
                val existing = readLists(prefs)
                val active = existing.filter { it.expiresAt == null || it.expiresAt > now }
                writeLists(prefs, active)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Falha na limpeza de listas expiradas", e)
        }
    }

    override suspend fun upsertList(list: HymnList): Result<Unit> {
        val result = updateLists { lists ->
            val withoutCurrent = lists.filterNot { it.id == list.id }
            withoutCurrent + list
        }
        if (result is Result.Success) syncInBackground { userRepository.upsertHymnList(list) }
        return result
    }

    private suspend fun updateLists(transform: (List<HymnList>) -> List<HymnList>): Result<Unit> {
        return try {
            context.dataStore.edit { prefs ->
                migrateLegacyIfNeeded(prefs)
                val existing = readLists(prefs)
                writeLists(prefs, transform(existing))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Falha ao atualizar listas", e)
        }
    }

    private fun parseLists(raw: String): List<HymnList> {
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                HymnList(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                    expiresAt = if (obj.has("expiresAt") && !obj.isNull("expiresAt"))
                        obj.getLong("expiresAt") else null,
                    hymnIds = obj.optJSONArray("hymnIds")?.let { ja ->
                        (0 until ja.length()).map { ja.getString(it) }
                    } ?: emptyList(),
                    updatedAt = obj.optLong("updatedAt", obj.optLong("createdAt", System.currentTimeMillis()))
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun serializeLists(lists: List<HymnList>): String {
        val arr = JSONArray()
        lists.forEach { list ->
            val obj = JSONObject().apply {
                put("id", list.id)
                put("name", list.name)
                put("createdAt", list.createdAt)
                put("expiresAt", list.expiresAt ?: JSONObject.NULL)
                put("hymnIds", JSONArray(list.hymnIds))
                put("updatedAt", list.updatedAt)
            }
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun readLists(prefs: Preferences): List<HymnList> {
        val order = prefs[Keys.LIST_ORDER]?.let { parseIds(it) } ?: emptyList()
        if (order.isNotEmpty()) {
            return order.mapNotNull { id -> prefs[Keys.listKey(id)]?.let { raw -> parseList(raw) } }
        }
        return parseLists(prefs[Keys.HYMN_LISTS] ?: "[]")
    }

    private fun writeLists(prefs: MutablePreferences, lists: List<HymnList>) {
        val ids = lists.map { it.id }
        prefs[Keys.LIST_ORDER] = JSONArray(ids).toString()
        lists.forEach { prefs[Keys.listKey(it.id)] = serializeList(it) }
    }

    private fun migrateLegacyIfNeeded(prefs: MutablePreferences) {
        if (prefs[Keys.LIST_ORDER] == null) {
            val legacy = parseLists(prefs[Keys.HYMN_LISTS] ?: "[]")
            if (legacy.isNotEmpty()) writeLists(prefs, legacy)
            prefs.remove(Keys.HYMN_LISTS)
        }
    }

    private suspend fun updateList(id: String, transform: (HymnList) -> HymnList): Result<Unit> =
        updateLists { lists -> lists.map { if (it.id == id) transform(it).copy(updatedAt = System.currentTimeMillis()) else it } }

    private fun syncListInBackground(id: String) {
        applicationScope.launch {
            val list = getAllLists().first().firstOrNull { it.id == id } ?: return@launch
            userRepository.upsertHymnList(list)
        }
    }

    private fun syncInBackground(operation: suspend () -> Result<Unit>) {
        applicationScope.launch { operation() }
    }

    private fun parseIds(raw: String): List<String> = try {
        val array = JSONArray(raw)
        (0 until array.length()).map(array::getString)
    } catch (_: Exception) { emptyList() }

    private fun parseList(raw: String): HymnList? = parseLists(JSONArray().put(JSONObject(raw)).toString()).firstOrNull()

    private fun serializeList(list: HymnList): String = serializeLists(listOf(list)).let { raw ->
        JSONArray(raw).getJSONObject(0).toString()
    }
}