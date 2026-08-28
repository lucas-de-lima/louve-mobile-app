package com.lucasdelima.louveapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import com.lucasdelima.louveapp.domain.repository.LocalSettingsRepository
import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.model.ThemeDefaults
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

    // Chaves para salvar configurações
    private object Keys {
        val APP_THEME = stringPreferencesKey("app_theme")
        val FONT_SCALE_FACTOR = floatPreferencesKey("font_scale_factor")
    }

    override val theme: Flow<String>
        get() = context.settingsDataStore.data.map { preferences ->
            // MODIFICAÇÃO SUTIL: Usando o nome do seu DefaultTheme para consistência.
            preferences[Keys.APP_THEME] ?: ThemeDefaults.THEME_ID
        }

    override suspend fun saveTheme(themeName: String) {
        context.settingsDataStore.edit { settings ->
            settings[Keys.APP_THEME] = themeName
        }
    }

    override val fontScaleFactor: Flow<Float>
        get() = context.settingsDataStore.data.map { preferences ->
            preferences[Keys.FONT_SCALE_FACTOR] ?: 1.0f // Default: tamanho normal
        }

    override suspend fun saveFontScaleFactor(factor: Float) {
        // Validar o valor antes de salvar
        val validatedFactor = factor.coerceIn(0.5f, 2.0f)
        context.settingsDataStore.edit { settings ->
            settings[Keys.FONT_SCALE_FACTOR] = validatedFactor
        }
    }
}
