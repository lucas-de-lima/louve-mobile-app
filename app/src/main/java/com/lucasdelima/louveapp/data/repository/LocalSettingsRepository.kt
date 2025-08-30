package com.lucasdelima.louveapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import com.lucasdelima.louveapp.domain.repository.LocalSettingsRepository
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.ui.theme.DefaultTheme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A anotação @Singleton para garantir
 * que o Hilt crie apenas uma unica instância para o app, que é a melhor prática
 * para repositórios e DataStore.
 */

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class LocalSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : com.lucasdelima.louveapp.domain.repository.LocalSettingsRepository {

    // Chave para salvar o nome do tema
    private object Keys {
        val APP_THEME = stringPreferencesKey("app_theme")
    }

    override val theme: Flow<String>
        get() = context.settingsDataStore.data.map { preferences ->
            // MODIFICAÇÃO SUTIL: Usando o nome do seu DefaultTheme para consistência.
            preferences[Keys.APP_THEME] ?: DefaultTheme.name
        }

    override suspend fun saveTheme(themeName: String) {
        context.settingsDataStore.edit { settings ->
            settings[Keys.APP_THEME] = themeName
        }
    }
    

}
