package com.lucasdelima.louveapp.data.repository

import com.lucasdelima.louveapp.domain.model.Result
import com.lucasdelima.louveapp.domain.model.UserSettings
import com.lucasdelima.louveapp.domain.repository.AuthRepository
import com.lucasdelima.louveapp.domain.repository.SettingsRepository
import com.lucasdelima.louveapp.domain.repository.UserRepository
import com.lucasdelima.louveapp.ui.theme.DefaultTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Este é o nosso "Repositório Mediador" para temas. Ele decide se deve ler/escrever
 * no DataStore local ou no Firestore remoto, com base no estado de login do usuário.
 * É esta classe que o SettingsViewModel usará.
 */
@Singleton
class DefaultSettingsRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val localSettingsRepository: LocalSettingsRepository,
    private val userRepository: UserRepository
) : SettingsRepository {

    override val theme: Flow<String> = authRepository.getCurrentUser().flatMapLatest { user ->
        if (user != null) {
            // Usuário LOGADO: Observa as configurações do Firestore.
            userRepository.getUserSettings().map { result ->
                (result as? Result.Success)?.data?.themeId ?: DefaultTheme.name
            }
        } else {
            // Usuário DESLOGADO: Observa as configurações do DataStore local.
            localSettingsRepository.theme
        }
    }

    override suspend fun saveTheme(themeName: String) {
        val user = authRepository.getCurrentUser().first()
        if (user != null) {
            // Usuário LOGADO: Salva no Firestore.
            userRepository.updateUserSettings(UserSettings(themeId = themeName))
        } else {
            // Usuário DESLOGADO: Salva no DataStore local.
            localSettingsRepository.saveTheme(themeName)
        }
    }
}
