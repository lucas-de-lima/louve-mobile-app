package com.lucasdelima.louveapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Extensão para criar a instância do DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LocalSettingsRepository @Inject constructor(
    private val context: Context
) : SettingsRepository {

    // Chave para salvar o nome do tema
    private object Keys {
        val APP_THEME = stringPreferencesKey("app_theme")
    }

    override val theme: Flow<String>
        get() = context.dataStore.data.map { preferences ->
            // Lê a preferência; se não existir, retorna "DefaultTheme" como padrão
            preferences[Keys.APP_THEME] ?: "DefaultTheme"
        }

    override suspend fun saveTheme(themeName: String) {
        context.dataStore.edit { settings ->
            settings[Keys.APP_THEME] = themeName
        }
    }
}