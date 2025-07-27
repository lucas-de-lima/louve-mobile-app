package com.lucasdelima.louveapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.repository.LocalFavoritesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


/**
 *
 * Implementação concreta que usa Jetpack DataStore para persistir os IDs dos
 * hinos favoritos no dispositivo do usuário.
 */

// Cria uma instância singleton do DataStore para toda a aplicação.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "louve_local_favorites")

@Singleton
class DataStoreLocalFavoritesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : LocalFavoritesRepository {

    private object PreferencesKeys {
        val FAVORITE_HYMN_IDS = stringSetPreferencesKey("favorite_hymn_ids")
    }

    override fun getFavorites(): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.FAVORITE_HYMN_IDS] ?: emptySet()
        }
    }

    override suspend fun addFavorite(hymnId: String): Result<Unit> {
        return try {
            context.dataStore.edit { preferences ->
                val currentFavorites = preferences[PreferencesKeys.FAVORITE_HYMN_IDS] ?: emptySet()
                preferences[PreferencesKeys.FAVORITE_HYMN_IDS] = currentFavorites + hymnId
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Falha ao salvar favorito localmente.", e)
        }
    }

    override suspend fun removeFavorite(hymnId: String): Result<Unit> {
        return try {
            context.dataStore.edit { preferences ->
                val currentFavorites = preferences[PreferencesKeys.FAVORITE_HYMN_IDS] ?: emptySet()
                preferences[PreferencesKeys.FAVORITE_HYMN_IDS] = currentFavorites - hymnId
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Falha ao remover favorito localmente.", e)
        }
    }

    override suspend fun clearFavorites(): Result<Unit> {
        return try {
            context.dataStore.edit { preferences ->
                preferences.remove(PreferencesKeys.FAVORITE_HYMN_IDS)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Falha ao limpar favoritos locais.", e)
        }
    }
}
